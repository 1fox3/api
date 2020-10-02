package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 股票实时信息数据
 * @author lusongsong
 * @date 2020/3/30 18:01
 */
@Component
public class StockRealtimeRankSchedule extends StockBaseSchedule {

    /**
     * 每分钟执行一次
     */
    @LogShowTimeAnt
    public void stockRealtimeRank() {
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
                BigDecimal openPrice = stockRealtimePo.getOpenPrice();
                //昨日收盘价
                BigDecimal preClosePrice = stockRealtimePo.getPreClosePrice();
                if (0 == openPrice.compareTo(BigDecimal.ZERO)
                        || 0 == preClosePrice.compareTo(BigDecimal.ZERO)) {
                    continue;
                }
                //增幅
                BigDecimal currentPrice = stockRealtimePo.getCurrentPrice();
                BigDecimal uptickRate = stockRealtimePo.getUptickRate();
                //波动
                BigDecimal surgeRate = stockRealtimePo.getSurgeRate();
                //成交量
                Long dealNum = stockRealtimePo.getDealNum();
                //成交金额
                BigDecimal dealMoney = stockRealtimePo.getDealMoney();
                //交易状态
                String dealStatus = stockRealtimePo.getDealStatus();
                //统计停牌数
                if (!("00").equals(dealStatus)) {
                    stopNum++;
                }

                if (null != currentPrice) {
                    priceSet.add(new DefaultTypedTuple(stockId, currentPrice.doubleValue()));
                }
                if (null != uptickRate) {
                    uptickRateSet.add(new DefaultTypedTuple(stockId, uptickRate.doubleValue()));
                }
                if (null != surgeRate) {
                    surgeRateSet.add(new DefaultTypedTuple(stockId, surgeRate.doubleValue()));
                }
                if (null != dealNum) {
                    dealNumSet.add(new DefaultTypedTuple(stockId, dealNum.doubleValue()));
                }
                if (null != dealMoney) {
                    dealMoneySet.add(new DefaultTypedTuple(stockId, dealMoney.doubleValue()));
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
    public void stockRealtimeUptickRateStatistics() {
        Map<String, Integer> uptickRateStatisticsMap = new  LinkedHashMap<>();
        Map<String, List<Double>> scoreMap = new LinkedHashMap<String, List<Double>>(){{
            put("up", Arrays.asList(0.00001, 100.0));
            put("upLimit", Arrays.asList(0.09700, 100.0));
            put("down", Arrays.asList(-1.0, -0.00001));
            put("downLimit", Arrays.asList(-1.0, -0.09701));
            put("flat", Arrays.asList(-0.00001, 0.00001));
//            put("scopeOne", Arrays.asList(-1.0, -0.07000));
//            put("scopeTwo", Arrays.asList(-0.06999, -0.05000));
//            put("scopeThree", Arrays.asList(-0.04999, -0.03000));
//            put("scopeFour", Arrays.asList(-0.02999, -0.00001));
//            put("scopeFive", Arrays.asList(0.00001, 0.02999));
//            put("scopeSix", Arrays.asList(0.03000, 0.04999));
//            put("scopeSeven", Arrays.asList(0.05000, 0.06999));
//            put("scopeEight", Arrays.asList(0.07000, 100.0));
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
