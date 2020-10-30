package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.stock.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.service.third.stock.sina.api.SinaStockBaseApi;
import com.fox.api.util.StockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 股票代码扫描
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Component
public class StockScanSchedule extends StockBaseSchedule {
    @Autowired
    StockIntoListSchedule stockIntoListSchedule;
    /**
     * 单词扫描股票代码数
     */
    private static Integer ScanOnceLimit = 300;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SinaRealtime sinaRealtime;
    /**
     * 最新交易日
     */
    private String lastDealDate;
    /**
     * 股票代码扫描范围
     */
    private static Map<Integer, Integer> stockScanScopeConfig = new LinkedHashMap<Integer, Integer>(){{
        put(StockConst.SM_SH, 1000000);
        put(StockConst.SM_SZ, 1000000);
        put(StockConst.SM_HK, 100000);
    }};
    /**
     * 暂未发现扫描规律的股票id
     */
    private static Map<Integer, List<String>> specialStockCodeMap = new LinkedHashMap<Integer, List<String>>(){{
        put(StockConst.SM_HK, Arrays.asList(
              "HSI",//恒生指数
              "HSCEI"//国企指数
        ));
    }};

    /**
     * 扫描股票代码列表
     * @param stockCodeList
     */
    private void scanStockCodeList(List<StockEntity> stockCodeList) {
        Map<String, StockRealtimePo> sinaStockRealtimeEntityMap = sinaRealtime.getRealtimeData(stockCodeList);
        for (StockEntity stockEntity : stockCodeList) {
            if (sinaStockRealtimeEntityMap.containsKey(stockEntity.getStockCode())) {
                StockRealtimePo stockRealtimePo = sinaStockRealtimeEntityMap.get(stockEntity.getStockCode());
                if (null != stockRealtimePo
                        && null != stockRealtimePo.getStockName()
                        && !stockRealtimePo.getStockName().equals("")) {
                    StockEntity dbStockEntity = stockMapper.getByStockCode(
                            stockEntity.getStockCode(), stockEntity.getStockMarket()
                    );
                    if (null != dbStockEntity) {
                        stockEntity = dbStockEntity;
                    }
                    syncStockDealStatus(stockEntity, stockRealtimePo);
                }
            }
        }
        stockCodeList.clear();
    }

    /**
     * 分批获取需要扫描的股票代码
     * @param stockMarket
     * @param maxLimit
     */
    private void scanStockMarket(Integer stockMarket, Integer maxLimit) {
        List<StockEntity> stockEntityList = new LinkedList<>();
        Integer stockCodeLen = String.valueOf(maxLimit).length() - 1;
        Integer cCopies = 0;
        String stockCodePer = "";
        for (Integer i = 0; i < maxLimit; i++) {
            if (i == Math.pow(10, cCopies) || 0 == i) {
                stockCodePer = String.join("", Collections.nCopies(stockCodeLen - cCopies - 1, "0"));
            }
            if (i > Math.pow(10, cCopies)) {
                cCopies++;
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(stockCodePer);
            stringBuffer.append(i);
            StockEntity stockEntity = new StockEntity();
            stockEntity.setStockCode(stringBuffer.toString());
            stockEntity.setStockMarket(stockMarket);
            stockEntityList.add(stockEntity);
            if (stockEntityList.size() >= ScanOnceLimit || maxLimit.equals(i)) {
                try {
                    this.scanStockCodeList(stockEntityList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 扫描暂未发现规律的股票代码
     * @param stockMarket
     */
    private void scanSpecialStockCode(Integer stockMarket) {
        if (specialStockCodeMap.containsKey(stockMarket)) {
            List<String> specialStockCodeList = specialStockCodeMap.get(stockMarket);
            List<StockEntity> stockEntityList = new LinkedList<>();
            for (String stockCode : specialStockCodeList) {
                StockEntity stockEntity = new StockEntity();
                stockEntity.setStockCode(stockCode);
                stockEntity.setStockMarket(stockMarket);
                stockEntityList.add(stockEntity);
            }
            if (!stockEntityList.isEmpty()) {
                try {
                    this.scanStockCodeList(stockEntityList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

        }
    }

    /**
     * 扫描股票代码
     * 对应新浪和网易的股票代码
     */
    @LogShowTimeAnt
    public void stockCodeScan() {
        for (Integer stockMarket : StockScanSchedule.stockScanScopeConfig.keySet()) {
            lastDealDate = StockUtil.lastDealDate(stockMarket);
            this.scanStockMarket(stockMarket, StockScanSchedule.stockScanScopeConfig.get(stockMarket));
            this.scanSpecialStockCode(stockMarket);
        }
        stockMapper.optimize();
    }

    /**
     * 扫描已经扫描出来的股票代码，同步当前交易状态
     * 补网易股票代码
     */
    @LogShowTimeAnt
    public void stockDealStatusScan() {
        if (StockUtil.todayIsDealDate(StockConst.SM_A)) {
            Integer stockId = 0;
            for (Integer stockMarket : StockConst.SM_A_LIST) {
                stockId = 0;
                try{
                    while (true) {
                        List<StockEntity> stockEntityList = this.stockMapper.getTotalByType(
                                2,
                                stockId,
                                stockMarket,
                                ScanOnceLimit.toString()
                        );
                        if (null == stockEntityList || stockEntityList.isEmpty()) {
                            break;
                        }
                        Map<String, StockRealtimePo> sinaStockRealtimePoMap =
                                sinaRealtime.getRealtimeData(stockEntityList);
                        for (StockEntity stockEntity : stockEntityList) {
                            if (null == stockEntity) {
                                continue;
                            }
                            stockId = null == stockEntity.getId() ? stockId + 1 : stockEntity.getId();
                            String stockCode = stockEntity.getStockCode();
                            if (sinaStockRealtimePoMap.containsKey(stockCode)) {
                                StockRealtimePo stockRealtimePo = sinaStockRealtimePoMap.get(stockCode);
                                //本地标记当前无交易或者接口获取的交易状态不正常则更新
                                if (1 == stockEntity.getStockStatus() || "00" != stockRealtimePo.getDealStatus()) {
                                    try {
                                        syncStockDealStatus(stockEntity, sinaStockRealtimePoMap.get(stockCode));
                                    } catch (Exception e) {
                                        logger.error(Integer.toString(stockId));
                                        logger.error(e.getMessage());
                                    }
                                }
                            }

                        }
                        if (stockEntityList.size() < ScanOnceLimit) {
                            break;
                        }
                    }
                } catch (Exception e){
                    logger.error(Integer.toString(stockId));
                    logger.error(e.getMessage());
                }
            }
        }
        stockIntoListSchedule.refreshStockCacheInfo();
    }

    /**
     * 同步交易信息
     * @param stockEntity
     * @param sinaStockRealtimePo
     */
    private void syncStockDealStatus(StockEntity stockEntity, StockRealtimePo sinaStockRealtimePo) {
        if (null == sinaStockRealtimePo || null == sinaStockRealtimePo.getStockName()
                || sinaStockRealtimePo.getStockName().isEmpty()) {
            return;
        }
        stockEntity.setStockName(sinaStockRealtimePo.getStockName());
        stockEntity.setStockNameEn(null == sinaStockRealtimePo.getStockNameEn()
                ? "" : sinaStockRealtimePo.getStockNameEn());

        //股票状态
        Integer stockStatus = 0;
        if (!StockUtil.lastDealDate(stockEntity.getStockMarket()).equals(sinaStockRealtimePo.getCurrentDate())
                || SinaStockBaseApi.noDealStatusList.contains(sinaStockRealtimePo.getDealStatus())
        ) {
            stockStatus = 1;
        }
        stockEntity.setStockStatus(stockStatus);
        //判定类别
        StockKindInfoProperty stockKindInfoEntity = stockUtilService.getStockKindInfo(
                stockEntity.getStockCode(), stockEntity.getStockMarket()
        );
        stockEntity.setStockType(null == stockKindInfoEntity.getStockType() ?
                0 : stockKindInfoEntity.getStockType());
        stockEntity.setStockKind(null == stockKindInfoEntity.getStockKind() ?
                0 : stockKindInfoEntity.getStockKind());
        stockEntity.setDealDate(
                null == sinaStockRealtimePo.getCurrentDate() ? "1900-01-01" : sinaStockRealtimePo.getCurrentDate()
        );
        stockEntity.setDealStatus(
                null == sinaStockRealtimePo.getDealStatus() ? "" : sinaStockRealtimePo.getDealStatus()
        );
        stockEntity.setUnknownInfo(
                null == sinaStockRealtimePo.getUnknownKeyList()
                        ? "" : sinaStockRealtimePo.getUnknownKeyList().toString()
        );
        try {
            if (null != stockEntity.getId()) {
                stockMapper.update(stockEntity);
            } else {
                stockMapper.insert(stockEntity);
            }
        } catch (Exception e) {
            logger.error(stockEntity.toString());
            logger.error(e.getMessage());
        }
    }
}
