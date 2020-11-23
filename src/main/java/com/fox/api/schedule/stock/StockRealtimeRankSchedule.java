package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.spider.stock.api.sina.SinaRealtimeDealInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 股票实时信息数据
 *
 * @author lusongsong
 * @date 2020/3/30 18:01
 */
@Component
public class StockRealtimeRankSchedule extends StockBaseSchedule {
    /**
     * 排行统计
     */
    public void syncStockRealtimeRank() {
        Long onceLimit = (long) 200;
        List hashKeyList = new ArrayList();
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            String codeListCacheKey = redisStockCodeList + ":" + stockMarket;
            String infoHashCacheKey = redisRealtimeStockInfoHash + ":" + stockMarket;
            String priceZSetKey = redisRealtimeRankPriceZSet + ":" + stockMarket;
            String uptickRateZSetKey = redisRealtimeRankUptickRateZSet + ":" + stockMarket;
            String surgeRateZSetKey = redisRealtimeRankSurgeRateZSet + ":" + stockMarket;
            String dealNumZSetKey = redisRealtimeRankDealNumZSet + ":" + stockMarket;
            String dealMoneyZSetKey = redisRealtimeRankDealMoneyZSet + ":" + stockMarket;
            String stopStatisticsCacheKey = stockRealtimeStockStopStatistics + ":" + stockMarket;
            Long codeListSize = stockRedisUtil.lSize(codeListCacheKey);
            int stopNum = 0;
            for (Long i = Long.valueOf(0); i < codeListSize; i += onceLimit) {
                List<Object> stockCodeList = stockRedisUtil.lRange(codeListCacheKey, i, i + onceLimit - (long) 1);
                if (null == stockCodeList || 0 >= stockCodeList.size()) {
                    continue;
                }
                hashKeyList.clear();
                for (Object stockCode : stockCodeList) {
                    hashKeyList.add(stockCode.toString());
                }
                List<Object> stockInfoList = stockRedisUtil.hMultiGet(
                        infoHashCacheKey,
                        hashKeyList
                );
                Set<DefaultTypedTuple> priceSet = new HashSet<>();
                Set<DefaultTypedTuple> uptickRateSet = new HashSet<>();
                Set<DefaultTypedTuple> surgeRateSet = new HashSet<>();
                Set<DefaultTypedTuple> dealNumSet = new HashSet<>();
                Set<DefaultTypedTuple> dealMoneySet = new HashSet<>();
                for (int j = 0; j < stockCodeList.size(); j++) {
                    String stockCode = (String) stockCodeList.get(j);
                    SinaRealtimeDealInfoPo sinaRealtimeDealInfoPo = (SinaRealtimeDealInfoPo) stockInfoList.get(j);
                    if (null == stockCode || null == sinaRealtimeDealInfoPo) {
                        continue;
                    }

                    //今日开盘价
                    BigDecimal openPrice = sinaRealtimeDealInfoPo.getOpenPrice();
                    //昨日收盘价
                    BigDecimal preClosePrice = sinaRealtimeDealInfoPo.getPreClosePrice();
                    if (0 == openPrice.compareTo(BigDecimal.ZERO)
                            || 0 == preClosePrice.compareTo(BigDecimal.ZERO)) {
                        continue;
                    }
                    //增幅
                    BigDecimal currentPrice = sinaRealtimeDealInfoPo.getCurrentPrice();
                    BigDecimal uptickRate = sinaRealtimeDealInfoPo.getUptickRate();
                    //波动
                    BigDecimal surgeRate = sinaRealtimeDealInfoPo.getSurgeRate();
                    //成交量
                    Long dealNum = sinaRealtimeDealInfoPo.getDealNum();
                    //成交金额
                    BigDecimal dealMoney = sinaRealtimeDealInfoPo.getDealMoney();
                    //交易状态
                    String dealStatus = sinaRealtimeDealInfoPo.getDealStatus();
                    //统计停牌数
                    if (!("00").equals(dealStatus)) {
                        stopNum++;
                    }

                    if (null != currentPrice) {
                        priceSet.add(new DefaultTypedTuple(stockCode, currentPrice.doubleValue()));
                    }
                    if (null != uptickRate) {
                        uptickRateSet.add(new DefaultTypedTuple(stockCode, uptickRate.doubleValue()));
                    }
                    if (null != surgeRate) {
                        surgeRateSet.add(new DefaultTypedTuple(stockCode, surgeRate.doubleValue()));
                    }
                    if (null != dealNum) {
                        dealNumSet.add(new DefaultTypedTuple(stockCode, dealNum.doubleValue()));
                    }
                    if (null != dealMoney) {
                        dealMoneySet.add(new DefaultTypedTuple(stockCode, dealMoney.doubleValue()));
                    }
                }
                if (0 < priceSet.size()) {
                    stockRedisUtil.zAdd(priceZSetKey, priceSet);
                }
                if (0 < uptickRateSet.size()) {
                    stockRedisUtil.zAdd(uptickRateZSetKey, uptickRateSet);
                }
                if (0 < surgeRateSet.size()) {
                    stockRedisUtil.zAdd(surgeRateZSetKey, surgeRateSet);
                }
                if (0 < dealNumSet.size()) {
                    stockRedisUtil.zAdd(dealNumZSetKey, dealNumSet);
                }
                if (0 < dealMoneySet.size()) {
                    stockRedisUtil.zAdd(dealMoneyZSetKey, dealMoneySet);
                }
            }
            stockRedisUtil.set(stopStatisticsCacheKey, stopNum);
        }
    }

    /**
     * 实时增幅统计
     */
    public void syncStockRealtimeUptickRateStatistics() {
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            Map<String, Integer> uptickRateStatisticsMap = new LinkedHashMap<>();
            Map<String, List<Double>> scoreMap = new LinkedHashMap<String, List<Double>>() {{
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
                Set<Object> set = stockRedisUtil.zReverseRangeByScore(
                        redisRealtimeRankUptickRateZSet + ":" + stockMarket,
                        startScore,
                        endScore
                );
                uptickRateStatisticsMap.put(key, set.size());
            }
            uptickRateStatisticsMap.put("stop", (int) stockRedisUtil.get(stockRealtimeStockStopStatistics + ":" + stockMarket));
            stockRedisUtil.set(stockRealtimeStockUptickRateStatistics + ":" + stockMarket, uptickRateStatisticsMap);
        }
    }
}
