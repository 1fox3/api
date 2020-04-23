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
     * 没分钟执行一次
     */
    @LogShowTimeAnt
    @Scheduled(cron="0 * 9,10,11,13,14 * * 1-5")
    public void stockRealtimeRank() {
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
    }

    @LogShowTimeAnt
    @Scheduled(cron="*/2 * 9,10,11,13,14 * * 1-5")
    public void stockRealtimeUptickRateStatistics() {
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
        this.stockRedisUtil.set(this.stockRealtimeStockUptickRateStatistics, uptickRateStatisticsMap);
    }
}
