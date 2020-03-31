package com.fox.api.schedule.stock;

import com.fox.api.entity.po.third.stock.StockRealtimePo;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 股票实时信息数据
 */
@Component
public class StockRealtimeRankSchedule extends StockBaseSchedule {

    /**
     * 没分钟执行一次
     */
    @Scheduled(cron="0 * 9,10,11,13,14 * * 1-5")
    public void stockRealtimeRank() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("stockRealtimeRank:start:" + df.format(System.currentTimeMillis()));
        Long stockIdListSize = this.stockRedisUtil.lSize(this.redisStockIdList);
        Long onceLimit = (long)600;
        for (Long i = Long.valueOf(0); i < stockIdListSize; i += onceLimit) {
            List<Object> stockIdList = this.stockRedisUtil.lRange(this.redisStockIdList, i, i + onceLimit - (long)1);
            if (null == stockIdList || 0 >= stockIdList.size()) {
                continue;
            }
            List hashKeyList = new LinkedList();
            for (Object stockId : stockIdList) {
                hashKeyList.add(stockId.toString());
            }
            List<Object> stockInfoList = this.stockRedisUtil.hMultiGet(
                    this.redisRealtimeStockInfoHash,
                    hashKeyList
            );

            Set<DefaultTypedTuple> uptickRateSet = new HashSet<>();
            Set<DefaultTypedTuple> surgeRateSet = new HashSet<>();
            Set<DefaultTypedTuple> dealNumSet = new HashSet<>();
            Set<DefaultTypedTuple> dealMoneySet = new HashSet<>();
            for (int j = 0; j < stockIdList.size(); j++) {
                Integer stockId = (Integer) stockIdList.get(j);
                StockRealtimePo stockRealtimePo = (StockRealtimePo) stockInfoList.get(j);
                if (null == stockId || null == stockRealtimePo) {
                    continue;
                }

                //今日开盘价
                Float todayOpenPrice = stockRealtimePo.getTodayOpenPrice();
                //昨日收盘价
                Float yesterdayClosePrice = stockRealtimePo.getYesterdayClosePrice();
                //当前价格
                Float currentPrice = stockRealtimePo.getCurrentPrice();
                //今日最高价
                Float todayHighestPrice = stockRealtimePo.getTodayHighestPrice();
                //今日最低价
                Float todayLowestPrice = stockRealtimePo.getTodayLowestPrice();
                //成交股数
                Long dealNum = stockRealtimePo.getDealNum();
                //成交金额
                Double dealMoney = stockRealtimePo.getDealMoney();

                if (null == currentPrice || null == yesterdayClosePrice
                        || null == todayHighestPrice || null == todayLowestPrice || null == todayOpenPrice
                ) {
                    continue;
                }

                //增幅
                Double uptickRate = (double)(currentPrice - yesterdayClosePrice) / yesterdayClosePrice;
                //波动
                Double surgeRate = (double)(todayHighestPrice - todayLowestPrice) / todayOpenPrice;
                if (0 == yesterdayClosePrice || 0 == todayOpenPrice) {
                    continue;
                }
                if (null != uptickRate) {
                    uptickRateSet.add(new DefaultTypedTuple(stockId, (double)uptickRate));
                }
                if (null != surgeRate) {
                    surgeRateSet.add(new DefaultTypedTuple(stockId, (double)surgeRate));
                }
                if (null != dealNum) {
                    dealNumSet.add(new DefaultTypedTuple(stockId, (double)dealNum));
                }
                if (null != dealMoney) {
                    dealMoneySet.add(new DefaultTypedTuple(stockId, (double)dealMoney));
                }
            }
            if (0 < uptickRateSet.size()) {
                this.stockRedisUtil.zAdd(this.redisRealtimeRankUptickRateZSet, uptickRateSet);
            }
            if (0 < surgeRateSet.size()) {
                this.stockRedisUtil.zAdd(this.redisRealtimeRankSurgeRateZSet, surgeRateSet);
            }
            if (0 < dealNumSet.size()) {
                this.stockRedisUtil.zAdd(this.redisRealtimeRankDealNumZSet, dealNumSet);
            }
            if (0 < dealMoneySet.size()) {
                this.stockRedisUtil.zAdd(this.redisRealtimeRankDealMoneyZSet, dealMoneySet);
            }
        }
        System.out.println("stockRealtimeRank:end:" + df.format(System.currentTimeMillis()));
    }
}
