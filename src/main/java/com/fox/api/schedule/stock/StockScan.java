package com.fox.api.schedule.stock;

import com.fox.api.model.stock.entity.StockEntity;
import com.fox.api.model.stock.mapper.StockMapper;
import com.fox.api.service.third.stock.entity.StockRealtimeEntity;
import com.fox.api.service.third.stock.entity.StockRealtimeLineEntity;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.service.third.stock.sina.api.SinaStockBaseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StockScan {

    @Autowired
    private StockMapper stockMapper;

    private static Map<String, Map<String, String>> stockScanConfig = new LinkedHashMap<String, Map<String, String>>(){{
        put("sh", new HashMap<String, String>(){{
            put("maxLimit", "1000000");
            put("stockMarketId", "0");
        }});
        put("sz", new HashMap<String, String>(){{
            put("maxLimit", "1000000");
            put("stockMarketId", "1");
        }});
        put("hk", new HashMap<String, String>(){{
            put("maxLimit", "100000");
            put("stockMarketId", "2");
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
        StockRealtimeEntity stockRealtimeEntity = sinaRealtime.getRealtimeData(sinaStockCode);
        if (null != stockRealtimeEntity) {
            return stockRealtimeEntity.getCurrentDate();
        }
        return "";
    }

    /**
     * 扫描股票代码
     * 对应新浪和网易的股票代码
     */
    @Scheduled(cron="0 30 20 * * ?")
    public void stockCodeScan() {
        List<String> stockCodeList = new LinkedList<>();
        Map<String, String> sinaStockMarketPYMap = SinaStockBaseApi.stockMarketPYMap;
        Map<String, String> netsStockMarketPYMap = NetsStockBaseApi.stockMarketPYMap;
        List<String> netsStockCodePrefixList = NetsStockBaseApi.stockCodePrefix;
        SinaRealtime sinaRealtime = new SinaRealtime();
        NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
        for (String stockMarket : StockScan.stockScanConfig.keySet()) {
            if (!sinaStockMarketPYMap.containsKey(stockMarket) || !netsStockMarketPYMap.containsKey(stockMarket)) {
                continue;
            }
            String lastDealDate = this.getLastDealDate(stockMarket);
            String sinaStockMarketPY = sinaStockMarketPYMap.get(stockMarket);
            String netsStockMarketPY = netsStockMarketPYMap.get(stockMarket);

            Map<String, String> scanConfig = StockScan.stockScanConfig.get(stockMarket);
            int maxLimit = scanConfig.containsKey("maxLimit") && !scanConfig.get("maxLimit").equals("") ?
                    Integer.valueOf(scanConfig.get("maxLimit")) : 1000000;
            int stockMarketId = scanConfig.containsKey("stockMarketId") && !scanConfig.get("stockMarketId").equals("") ?
                    Integer.valueOf(scanConfig.get("stockMarketId")) : 0;

            int stockCodeLen = String.valueOf(maxLimit).length() - 1;
            for (int i = 0; i < maxLimit; i++) {
                String stockCode = String.valueOf(i);
                stockCode = String.join("", Collections.nCopies(stockCodeLen - stockCode.length(), "0"))
                        + stockCode;

                String sinaStockCode = sinaStockMarketPY + stockCode;

                stockCodeList.add(sinaStockCode);

                if (stockCodeList.size() >= 500 || maxLimit == i) {
                    Map<String, StockRealtimeEntity> sinaStockRealtimeEntityMap = sinaRealtime.getRealtimeData(stockCodeList);
                    for (String currentStockCode : stockCodeList) {
                        if (sinaStockRealtimeEntityMap.containsKey(currentStockCode)) {
                            StockRealtimeEntity stockRealtimeEntity = sinaStockRealtimeEntityMap.get(currentStockCode);
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
                                    StockRealtimeLineEntity stockRealtimeLineEntity =
                                            netsMinuteRealtime.getRealtimeData(netsStockInfoMap);
                                    if (null != stockRealtimeLineEntity.getStockName()
                                            && !stockRealtimeLineEntity.getStockName().equals("")
                                            && stockCodeName.equals(stockRealtimeLineEntity.getStockName())
                                    ) {
                                        netsStockCode = currentNetsStockCode;
                                        break;
                                    }
                                }
                                System.out.println(stockCode + "\t" + stockCodeName);
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
