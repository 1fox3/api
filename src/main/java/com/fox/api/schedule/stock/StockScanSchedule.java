package com.fox.api.schedule.stock;

import com.fox.api.property.stock.StockProperty;
import com.fox.api.entity.property.stock.StockKindInfoProperty;
import com.fox.api.entity.property.stock.StockMarketInfoProperty;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.service.stock.StockUtilService;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.service.third.stock.sina.api.SinaStockBaseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StockScanSchedule {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockUtilService stockUtilService;

    @Autowired
    private StockProperty stockConfig;

    private static Map<String, Map<String, String>> stockScanConfig = new LinkedHashMap<String, Map<String, String>>(){{
        put("sh", new HashMap<String, String>(){{
            put("maxLimit", "1000000");
        }});
        put("sz", new HashMap<String, String>(){{
            put("maxLimit", "1000000");
        }});
        put("hk", new HashMap<String, String>(){{
            put("maxLimit", "100000");
        }});
    }};

    /**
     * 获取股市的最新交易日
     * @param market
     * @return
     */
    private String getLastDealDate(String market) {
        SinaRealtime sinaRealtime = new SinaRealtime();
        String sinaStockCode = "sh000001";//上证指数
        if ("hk".equals(market)) {
            sinaStockCode = "hk00700";//腾讯
        }
        StockRealtimePo stockRealtimeEntity = sinaRealtime.getRealtimeData(sinaStockCode);
        if (null != stockRealtimeEntity) {
            return stockRealtimeEntity.getCurrentDate();
        }
        return "";
    }

    /**
     * 扫描股票代码
     * 对应新浪和网易的股票代码
     */
    @Scheduled(cron="0 0 1 * * 1-5")
    public void stockCodeScan() {
        List<String> stockCodeList = new LinkedList<>();
        Map<String, String> sinaStockMarketPYMap = SinaStockBaseApi.stockMarketPYMap;
        Map<String, String> netsStockMarketPYMap = NetsStockBaseApi.stockMarketPYMap;
        List<String> netsStockCodePrefixList = NetsStockBaseApi.stockCodePrefix;
        SinaRealtime sinaRealtime = new SinaRealtime();
        NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
        Map<String, StockMarketInfoProperty> stockMarketConfigMap = stockConfig.getMarket();
        for (String stockMarket : StockScanSchedule.stockScanConfig.keySet()) {
            if (!sinaStockMarketPYMap.containsKey(stockMarket) || !netsStockMarketPYMap.containsKey(stockMarket)) {
                continue;
            }
            StockMarketInfoProperty stockMarketInfoEntity = stockMarketConfigMap.containsKey(stockMarket) ?
                    stockMarketConfigMap.get(stockMarket) : new StockMarketInfoProperty();
            String lastDealDate = this.getLastDealDate(stockMarket);
            String sinaStockMarketPY = sinaStockMarketPYMap.get(stockMarket);
            String netsStockMarketPY = netsStockMarketPYMap.get(stockMarket);

            Map<String, String> scanConfig = StockScanSchedule.stockScanConfig.get(stockMarket);
            int maxLimit = scanConfig.containsKey("maxLimit") && !scanConfig.get("maxLimit").equals("") ?
                    Integer.valueOf(scanConfig.get("maxLimit")) : 1000000;
            int stockMarketId = null == stockMarketInfoEntity.getStockMarket() ?
                    0 : stockMarketInfoEntity.getStockMarket();

            int stockCodeLen = String.valueOf(maxLimit).length() - 1;
            for (int i = 0; i < maxLimit; i++) {
                String stockCode = String.valueOf(i);
                stockCode = String.join("", Collections.nCopies(stockCodeLen - stockCode.length(), "0"))
                        + stockCode;

                String sinaStockCode = sinaStockMarketPY + stockCode;

                stockCodeList.add(sinaStockCode);

                if (stockCodeList.size() >= 500 || maxLimit == i) {
                    Map<String, StockRealtimePo> sinaStockRealtimeEntityMap = sinaRealtime.getRealtimeData(stockCodeList);
                    for (String currentStockCode : stockCodeList) {
                        if (sinaStockRealtimeEntityMap.containsKey(currentStockCode)) {
                            StockRealtimePo stockRealtimeEntity = sinaStockRealtimeEntityMap.get(currentStockCode);
                            if (null != stockRealtimeEntity
                                    && null != stockRealtimeEntity.getStockName()
                                    && !stockRealtimeEntity.getStockName().equals("")) {
                                stockCode = currentStockCode.replace(sinaStockMarketPY, "");
                                String stockCodeName = stockRealtimeEntity.getStockName();
                                String netsStockCode = "";
                                for (String netsStockCodePrefix : netsStockCodePrefixList) {
                                    String currentNetsStockCode = netsStockCodePrefix + stockCode;
                                    Map<String, String> netsStockInfoMap = new HashMap<>();
                                    netsStockInfoMap.put("netsStockCode", currentNetsStockCode);
                                    netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
                                    StockRealtimeLinePo stockRealtimeLineEntity =
                                            netsMinuteRealtime.getRealtimeData(netsStockInfoMap);
                                    if (null != stockRealtimeLineEntity.getStockName()
                                            && !stockRealtimeLineEntity.getStockName().equals("")
                                            && stockCodeName.equals(stockRealtimeLineEntity.getStockName())
                                    ) {
                                        netsStockCode = currentNetsStockCode;
                                        break;
                                    }
                                }
                                StockEntity stockEntity = stockMapper.getByStockCode(stockCode, stockMarketId);
                                if (null == stockEntity) {
                                    stockEntity = new StockEntity();
                                }
                                stockEntity.setStockCode(stockCode);
                                stockEntity.setStockName(stockCodeName);
                                if (null != stockRealtimeEntity.getStockNameEn()) {
                                    stockEntity.setStockNameEn(stockRealtimeEntity.getStockNameEn());
                                }
                                stockEntity.setSinaStockCode(currentStockCode);
                                stockEntity.setNetsStockCode(netsStockCode);
                                stockEntity.setStockMarket(stockMarketId);
                                stockEntity.setStockStatus(lastDealDate.equals(stockRealtimeEntity.getCurrentDate()) ? 0 : 1);
                                //判定类别
                                StockKindInfoProperty stockKindInfoEntity = stockUtilService.getStockKindInfo(stockCode, stockMarketId);
                                stockEntity.setStockType(null == stockKindInfoEntity.getStockType() ?
                                        0 : stockKindInfoEntity.getStockType());
                                stockEntity.setStockKind(null == stockKindInfoEntity.getStockKind() ?
                                        0 : stockKindInfoEntity.getStockKind());
                                if (null != stockEntity.getId()) {
                                    stockMapper.update(stockEntity);
                                } else {
                                    stockMapper.insert(stockEntity);
                                }
                            }
                        }
                    }
                    stockCodeList.clear();
                }
            }
        }
    }
}
