package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 股票实时信息数据
 * @author lusongsong
 */
@Component
public class StockRealtimeRankSchedule extends StockBaseSchedule {

    /**
     * 每分钟执行一次
     */
    @LogShowTimeAnt
    //@Scheduled(cron="0 * 9,10,11,13,14 * * 1-5")
    public void stockRealtimeRank() {
        if (!this.isDealTime()) {
            return;
        }
        Long stockIdListSize = this.stockRedisUtil.lSize(this.redisStockIdList);
        int stopNum = 0;
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

            Set<DefaultTypedTuple> priceSet = new HashSet<>();
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
                if (0 == yesterdayClosePrice || 0 == todayOpenPrice) {
                    continue;
                }
                //增幅
                Double currentPrice = (double)stockRealtimePo.getCurrentPrice();
                Double uptickRate = (double)stockRealtimePo.getUptickRate();
                //波动
                Double surgeRate = (double)stockRealtimePo.getSurgeRate();
                //成交股数
                Double dealNum = (double)stockRealtimePo.getDealNum();
                //成交金额
                Double dealMoney = stockRealtimePo.getDealMoney();
                //交易状态
                String dealStatus = stockRealtimePo.getDealStatus();
                //统计停牌数
                if (!("00").equals(dealStatus)) {
                    stopNum++;
                }

                if (null != currentPrice) {
                    priceSet.add(new DefaultTypedTuple(stockId, currentPrice));
                }
                if (null != uptickRate) {
                    uptickRateSet.add(new DefaultTypedTuple(stockId, uptickRate));
                }
                if (null != surgeRate) {
                    surgeRateSet.add(new DefaultTypedTuple(stockId, surgeRate));
                }
                if (null != dealNum) {
                    dealNumSet.add(new DefaultTypedTuple(stockId, dealNum));
                }
                if (null != dealMoney) {
                    dealMoneySet.add(new DefaultTypedTuple(stockId, dealMoney));
                }
            }
            if (0 < priceSet.size()) {
                this.stockRedisUtil.zAdd(this.redisRealtimeRankPriceZSet, priceSet);
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
        this.stockRedisUtil.set(this.stockRealtimeStockStopStatistics, stopNum);
    }

    /**
     * 实时增幅统计
     */
    @LogShowTimeAnt
    //@Scheduled(cron="*/2 * 9,10,11,13,14 * * 1-5")
    public void stockRealtimeUptickRateStatistics() {
        if (!this.isDealTime()) {
            return;
        }
        Map<String, Integer> uptickRateStatisticsMap = new  LinkedHashMap<>();
        Map<String, List<Double>> scoreMap = new LinkedHashMap<String, List<Double>>(){{
            put("up", Arrays.asList(0.00001, 1.0));
            put("down", Arrays.asList(-1.0, -0.00001));
            put("zero", Arrays.asList(-0.00001, 0.00001));
            put("-3~0", Arrays.asList(-0.02999, -0.00001));
            put("-5~-3", Arrays.asList(-0.04999, -0.03000));
            put("-7~-5", Arrays.asList(-0.06999, -0.05000));
            put("-7", Arrays.asList(-1.0, -0.07000));
            put("0~3", Arrays.asList(0.00001, 0.02999));
            put("3~5", Arrays.asList(0.03000, 0.04999));
            put("5~7", Arrays.asList(0.05000, 0.06999));
            put("7", Arrays.asList(0.07000, 1.0));
        }};

        Double startScore;
        Double endScore;
        for (String key : scoreMap.keySet()) {
            List<Double> list = scoreMap.get(key);
            startScore = list.get(0);
            endScore = list.get(1);
            Set<Object> set = this.stockRedisUtil.zReverseRangeByScore(
                    this.redisRealtimeRankUptickRateZSet,
                    startScore,
                    endScore
            );
            uptickRateStatisticsMap.put(key, set.size());
        }
        uptickRateStatisticsMap.put("stop", (int)this.stockRedisUtil.get(this.stockRealtimeStockStopStatistics));
        this.stockRedisUtil.set(this.stockRealtimeStockUptickRateStatistics, uptickRateStatisticsMap);
    }
}
