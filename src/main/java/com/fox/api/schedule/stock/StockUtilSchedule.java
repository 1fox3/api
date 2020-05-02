package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.util.StockUtil;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
/**
 * 股票工具类任务，提供基本信息
 * @author lusongsong
 */
public class StockUtilSchedule extends StockBaseSchedule {

    /**
     * 获取最新交易日
     */
    @LogShowTimeAnt
    public void getStockMarketLastDealDate() {
        SinaRealtime sinaRealtime = new SinaRealtime();
        Map<String, String> marketCodeMap = new LinkedHashMap<>(3);
        marketCodeMap.put("sh", "sh000001");
        marketCodeMap.put("sz", "sz399001");
        marketCodeMap.put("hk", "hk00700");
        for (String stockMarket : marketCodeMap.keySet()) {
            StockRealtimePo stockRealtimeEntity = sinaRealtime.getRealtimeData(marketCodeMap.get(stockMarket));
            if (null != stockRealtimeEntity) {
                String lastDealDate = stockRealtimeEntity.getCurrentDate();
                if (null != lastDealDate && !lastDealDate.equals("")) {
                    this.stockRedisUtil.set(StockUtil.getLastDealDateCacheKey(stockMarket), lastDealDate);
                }
            }
        }
    }
}
