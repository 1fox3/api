package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
/**
 * 股票代码扫描
 * @author lusongsong
 */
public class StockScanSchedule extends StockBaseSchedule {
    @Autowired
    private SinaRealtime sinaRealtime;
    @Autowired
    private NetsMinuteRealtime netsMinuteRealtime;
    /**
     * 股票代码扫描范围
     */
    private static Map<String, Integer> stockScanScopeConfig = new LinkedHashMap<String, Integer>(){{
        put("sh", 1000000);
        put("sz", 1000000);
        put("hk", 100000);
    }};

    /**
     * 获取market的id
     * @param stockMarket
     * @return
     */
    private Integer getStockMarketId(String stockMarket) {
        Map<String, StockMarketInfoProperty> stockMarketConfigMap = stockConfig.getMarket();
        StockMarketInfoProperty stockMarketInfoProperty = stockMarketConfigMap.containsKey(stockMarket) ?
                stockMarketConfigMap.get(stockMarket) : new StockMarketInfoProperty();
        return stockMarketInfoProperty.getStockMarket();
    }

    /**
     * 获取网易股票接口对应的股票代码
     * @param netsStockMarketPY
     * @param stockCode
     * @param stockCodeName
     * @return
     */
    private String getNetsStockCode(String netsStockMarketPY, String stockCode, String stockCodeName) {
        List<String> netsStockCodePrefixList = NetsStockBaseApi.stockCodePrefix;
        for (String netsStockCodePrefix : netsStockCodePrefixList) {
            String currentNetsStockCode = netsStockCodePrefix + stockCode;
            Map<String, String> netsStockInfoMap = new HashMap<>(2);
            netsStockInfoMap.put("netsStockCode", currentNetsStockCode);
            netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
            StockRealtimeLinePo stockRealtimeLineEntity =
                    netsMinuteRealtime.getRealtimeData(netsStockInfoMap);
            if (null != stockRealtimeLineEntity.getStockName()
                    && !stockRealtimeLineEntity.getStockName().equals("")
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
    private void scanStockCodeList(String stockMarket, List<String> stockCodeList) {
        String sinaStockMarketPY = SinaStockBaseApi.stockMarketPYMap.get(stockMarket);
        String netsStockMarketPY = NetsStockBaseApi.stockMarketPYMap.get(stockMarket);
        String lastDealDate = StockUtil.getLastDealDate(stockMarket);
        Integer stockMarketId = this.getStockMarketId(stockMarket);
        Map<String, StockRealtimePo> sinaStockRealtimeEntityMap = sinaRealtime.getRealtimeData(stockCodeList);
        String stockCode;
        for (String currentStockCode : stockCodeList) {
            if (sinaStockRealtimeEntityMap.containsKey(currentStockCode)) {
                StockRealtimePo stockRealtimeEntity = sinaStockRealtimeEntityMap.get(currentStockCode);
                if (null != stockRealtimeEntity
                        && null != stockRealtimeEntity.getStockName()
                        && !stockRealtimeEntity.getStockName().equals("")) {
                    stockCode = currentStockCode.replace(sinaStockMarketPY, "");
                    String stockName = stockRealtimeEntity.getStockName();
                    String netsStockCode = this.getNetsStockCode(netsStockMarketPY, stockCode, stockName);
                    StockEntity stockEntity = stockMapper.getByStockCode(stockCode, stockMarketId);
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
                    stockEntity.setStockMarket(stockMarketId);
                    int stockStatus = 0;
                    if (lastDealDate.equals(stockRealtimeEntity.getCurrentDate())
                            && !"-2".equals(stockRealtimeEntity.getDealStatus())
                    ) {
                        stockStatus = 1;
                    }
                    stockEntity.setStockStatus(stockStatus);
                    //判定类别
                    StockKindInfoProperty stockKindInfoEntity = stockUtilService.getStockKindInfo(stockCode, stockMarketId);
                    stockEntity.setStockType(null == stockKindInfoEntity.getStockType() ?
                            0 : stockKindInfoEntity.getStockType());
                    stockEntity.setStockKind(null == stockKindInfoEntity.getStockKind() ?
                            0 : stockKindInfoEntity.getStockKind());
                    try {
                        if (null != stockEntity.getId()) {
                            stockMapper.update(stockEntity);
                        } else {
                            stockMapper.insert(stockEntity);
                        }
                    } catch (Exception e) {
                        System.out.println(stockEntity);
                        e.printStackTrace();
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
    private void scanStockMarket(String stockMarket, Integer maxLimit) {
        List<String> stockCodeList = new LinkedList<>();
        String sinaStockMarketPY = SinaStockBaseApi.stockMarketPYMap.get(stockMarket);

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

            String sinaStockCode = sinaStockMarketPY + stockCodePer + i;

            stockCodeList.add(sinaStockCode);

            if (stockCodeList.size() >= 500 || maxLimit.equals(i)) {
                try {
                    this.scanStockCodeList(stockMarket, stockCodeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @LogShowTimeAnt
    /**
     * 扫描股票代码
     * 对应新浪和网易的股票代码
     */
    public void stockCodeScan() {
        for (String stockMarket : StockScanSchedule.stockScanScopeConfig.keySet()) {
            this.scanStockMarket(stockMarket, StockScanSchedule.stockScanScopeConfig.get(stockMarket));
        }
    }
}
