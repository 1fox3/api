package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.admin.DateType;
import com.fox.api.service.admin.impl.DateTypeImpl;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 股票工具类任务，提供基本信息
 * @author lusongsong
 * @date 2020/5/2 14:20
 */
@Component
public class StockUtilSchedule extends StockBaseSchedule {
    @Autowired
    DateType dateType;

    /**
     * 获取最新交易日,当发现交易日发生变化时，更新上个交易日
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
                    String cacheKey = StockUtil.getLastDealDateCacheKey(stockMarket);
                    if (!lastDealDate.equals(this.stockRedisUtil.get(cacheKey))) {
                        refreshPreDealDate(stockMarket);
                        refreshNextDealDate(stockMarket, lastDealDate);
                    }
                    this.stockRedisUtil.set(cacheKey, lastDealDate);
                }
            }
        }
    }

    /**
     * 刷新上个交易日
     * @param stockMarket
     */
    private void refreshPreDealDate(String stockMarket) {
        this.stockRedisUtil.set(
                StockUtil.getPreDealDateCacheKey(stockMarket),
                this.stockRedisUtil.get(StockUtil.getLastDealDateCacheKey(stockMarket))
        );
    }

    /**
     * 刷新下个交易日
     * @param stockMarket
     */
    private void refreshNextDealDate(String stockMarket, String lastDealDate) {
        int dayInWeekNum = 0;
        for (int i = 0; i < 30; i++) {
            String currentDate = DateUtil.getRelateDate(
                    lastDealDate, 0, 0, i, DateUtil.DATE_FORMAT_1
            );
            try {
                dayInWeekNum = DateUtil.getDayInWeekNum(currentDate, DateUtil.DATE_FORMAT_1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (1 <= dayInWeekNum && 5 >= dayInWeekNum
                    && DateTypeImpl.DATE_TYPE_WORKDAY.equals(dateType.getByDate(currentDate))) {
                this.stockRedisUtil.set(
                        StockUtil.getNextDealDateCacheKey(stockMarket),
                        currentDate
                );
                break;
            }
        }
    }
}
