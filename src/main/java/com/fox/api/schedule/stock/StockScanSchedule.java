package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.entity.property.stock.StockMarketInfoProperty;
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
public class StockScanSchedule extends StockBaseSchedule {
    /**
     * 单词扫描股票代码数
     */
    private static Integer ScanOnceLimit = 200;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SinaRealtime sinaRealtime;
    @Autowired
    private NetsMinuteRealtime netsMinuteRealtime;
    /**
     * 最新交易日
     */
    private String lastDealDate;
    /**
     * 新浪数据中心股票集市前缀
     */
    private String sinaStockMarketPY;
    /**
     * 网易数据中心股票集市前缀
     */
    private String netsStockMarketPY;
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
     * 获取网易股票接口对应的股票代码
     * @param stockCode
     * @param stockCodeName
     * @return
     */
    private String getNetsStockCode(String stockCode, String stockCodeName) {
        for (String netsStockCodePrefix : NetsStockBaseApi.stockCodePrefix) {
            String currentNetsStockCode = netsStockCodePrefix + stockCode;
            Map<String, String> netsStockInfoMap = new HashMap<>(2);
            netsStockInfoMap.put("netsStockCode", currentNetsStockCode);
            netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
            StockRealtimeLinePo stockRealtimeLineEntity =
                    netsMinuteRealtime.getRealtimeData(netsStockInfoMap);
            if (null != stockRealtimeLineEntity
                    && null != stockRealtimeLineEntity.getStockName()
                    && stockCodeName.equals(stockRealtimeLineEntity.getStockName())
            ) {
                return currentNetsStockCode;
            }
        }
        return "";
    }

    /**
     * 扫描股票代码列表
     * @param stockMarket
     * @param stockCodeList
     */
    private void scanStockCodeList(Integer stockMarket, List<String> stockCodeList) {
        Map<String, StockRealtimePo> sinaStockRealtimeEntityMap = sinaRealtime.getRealtimeData(stockCodeList);
        String stockCode;
        int stockStatus;
        for (String currentStockCode : stockCodeList) {
            if (sinaStockRealtimeEntityMap.containsKey(currentStockCode)) {
                StockRealtimePo stockRealtimeEntity = sinaStockRealtimeEntityMap.get(currentStockCode);
                if (null != stockRealtimeEntity
                        && null != stockRealtimeEntity.getStockName()
                        && !stockRealtimeEntity.getStockName().equals("")) {
                    stockCode = currentStockCode.replace(sinaStockMarketPY, "");
                    String stockName = stockRealtimeEntity.getStockName();
                    String netsStockCode = this.getNetsStockCode(stockCode, stockName);
                    StockEntity stockEntity = stockMapper.getByStockCode(stockCode, stockMarket);
                    if (null == stockEntity) {
                        stockEntity = new StockEntity();
                    }
                    stockEntity.setStockCode(stockCode);
                    stockEntity.setStockName(stockName);
                    if (null != stockRealtimeEntity.getStockNameEn()) {
                        stockEntity.setStockNameEn(stockRealtimeEntity.getStockNameEn());
                    }
                    stockEntity.setSinaStockCode(currentStockCode);
                    stockEntity.setNetsStockCode(netsStockCode);
                    stockEntity.setStockMarket(stockMarket);
                    stockStatus = 0;
                    if (!lastDealDate.equals(stockRealtimeEntity.getCurrentDate())
                            || "-2".equals(stockRealtimeEntity.getDealStatus())
                    ) {
                        stockStatus = 1;
                    }
                    stockEntity.setStockStatus(stockStatus);
                    //判定类别
                    StockKindInfoProperty stockKindInfoEntity = stockUtilService.getStockKindInfo(stockCode, stockMarket);
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
        List<String> stockCodeList = new LinkedList<>();
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
            stringBuffer.append(sinaStockMarketPY);
            stringBuffer.append(stockCodePer);
            stringBuffer.append(i);
            stockCodeList.add(stringBuffer.toString());

            if (stockCodeList.size() >= ScanOnceLimit || maxLimit.equals(i)) {
                try {
                    this.scanStockCodeList(stockMarket, stockCodeList);
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
            List<String> stockCodeList = new LinkedList<>();
            for (String stockCode : specialStockCodeList) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(sinaStockMarketPY);
                stringBuffer.append(stockCode);
                stockCodeList.add(stringBuffer.toString());
            }
            if (!stockCodeList.isEmpty()) {
                try {
                    this.scanStockCodeList(stockMarket, stockCodeList);
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
            lastDealDate = StockUtil.getLastDealDate(stockMarket);
            sinaStockMarketPY = SinaStockBaseApi.getSinaStockMarketPY(stockMarket);
            netsStockMarketPY = NetsStockBaseApi.getNetsStockMarketPY(stockMarket);
            this.scanStockMarket(stockMarket, StockScanSchedule.stockScanScopeConfig.get(stockMarket));
            this.scanSpecialStockCode(stockMarket);
        }
        stockMapper.optimize();
    }
}
