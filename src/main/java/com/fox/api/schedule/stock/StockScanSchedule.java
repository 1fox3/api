package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
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
public class StockScanSchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 单词扫描股票代码数
     */
    private static Integer ScanOnceLimit = 200;
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
        int stockStatus;
        for (StockEntity stockEntity : stockCodeList) {
            if (sinaStockRealtimeEntityMap.containsKey(stockEntity.getStockCode())) {
                StockRealtimePo stockRealtimeEntity = sinaStockRealtimeEntityMap.get(stockEntity.getStockCode());
                if (null != stockRealtimeEntity
                        && null != stockRealtimeEntity.getStockName()
                        && !stockRealtimeEntity.getStockName().equals("")) {
                    String stockName = stockRealtimeEntity.getStockName();
                    StockEntity dbStockEntity = stockMapper.getByStockCode(
                            stockEntity.getStockCode(), stockEntity.getStockMarket()
                    );
                    if (null != dbStockEntity) {
                        stockEntity = dbStockEntity;
                    }
                    stockEntity.setStockName(stockName);
                    if (null != stockRealtimeEntity.getStockNameEn()) {
                        stockEntity.setStockNameEn(stockRealtimeEntity.getStockNameEn());
                    }
                    stockStatus = 0;
                    if (!lastDealDate.equals(stockRealtimeEntity.getCurrentDate())
                            || "-2".equals(stockRealtimeEntity.getDealStatus())
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
                            null == stockRealtimeEntity.getCurrentDate() ? "1900-01-01" : stockRealtimeEntity.getCurrentDate()
                    );
                    stockEntity.setDealStatus(
                            null == stockRealtimeEntity.getDealStatus() ? "" : stockRealtimeEntity.getDealStatus()
                    );
                    stockEntity.setUnknownInfo(
                            null == stockRealtimeEntity.getUnknownKeyList()
                                    ? "" : stockRealtimeEntity.getUnknownKeyList().toString()
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
     * 扫描已经扫描出来的股票代码，同比当前交易状态
     * 补网易股票代码
     */
    @LogShowTimeAnt
    public void stockDealStatusScan() {
        if (StockUtil.todayIsDealDate(StockConst.SM_A)) {
            aStockMarketTopIndexScan(this);
            aStockMarketScan(this);
        }
    }

    /**
     * 处理单只股票
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity.getStockCode() || stockEntity.getStockCode().isEmpty()
                || null == stockEntity.getStockMarket() || !StockConst.SM_ALL.contains(stockEntity.getStockMarket())) {
            return;
        }
        StockRealtimePo sinaStockRealtimePo = sinaRealtime.getRealtimeData(stockEntity);
        if (null == sinaStockRealtimePo || null == sinaStockRealtimePo.getStockName()
                || sinaStockRealtimePo.getStockName().isEmpty()) {
            return;
        }
        stockEntity.setStockName(sinaStockRealtimePo.getStockName());
        stockEntity.setStockNameEn(sinaStockRealtimePo.getStockNameEn());

        //股票状态
        Integer stockStatus = 0;
        if (!StockUtil.lastDealDate(stockEntity.getStockMarket()).equals(sinaStockRealtimePo.getCurrentDate())
                || "-2".equals(sinaStockRealtimePo.getDealStatus())
        ) {
            stockStatus = 1;
        }
        stockEntity.setStockStatus(stockStatus);

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
            stockMapper.update(stockEntity);
        } catch (Exception e) {
            logger.error(stockEntity.toString());
            logger.error(e.getMessage());
        }
    }
}
