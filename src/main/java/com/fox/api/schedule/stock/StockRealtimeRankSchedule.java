package com.fox.api.schedule.stock;

import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;
import com.fox.spider.stock.service.StockToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
     * 股票工具类
     */
    @Autowired
    StockToolService stockToolService;
    /**
     * 排行统计
     */
    public void syncStockRealtimeRank() {
        Long onceLimit = (long) 200;
        List hashKeyList = new ArrayList();
        String schedule = "StockRealtimeRankSchedule:syncStockRealtimeRank";
        String cacheNamePre = "pre";
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            if (!realtimeDealScheduleCanRun(stockMarket, schedule)) {
                continue;
            }
            String codeListCacheKey = redisStockCodeList + ":" + stockMarket;
            String infoHashCacheKey = redisRealtimeStockInfoHash + ":" + stockMarket;
            String priceZSetKey = redisRealtimeRankPriceZSet + ":" + stockMarket;
            String uptickRateZSetKey = redisRealtimeRankUptickRateZSet + ":" + stockMarket;
            String surgeRateZSetKey = redisRealtimeRankSurgeRateZSet + ":" + stockMarket;
            String dealNumZSetKey = redisRealtimeRankDealNumZSet + ":" + stockMarket;
            String dealMoneyZSetKey = redisRealtimeRankDealMoneyZSet + ":" + stockMarket;
            String upLimitListKey = stockRealtimeStockRankUpLimitList + ":" + stockMarket;
            String downLimitListKey = stockRealtimeStockRankDownLimitList + ":" + stockMarket;
            Long codeListSize = stockRedisUtil.lSize(codeListCacheKey);
            List<String> upLimitStockCodeList = new ArrayList<>();
            List<String> downLimitStockCodeList = new ArrayList<>();
            BigDecimal upRateLimit = null;
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
                    //上个交易日收盘价
                    BigDecimal preClosePrice = sinaRealtimeDealInfoPo.getPreClosePrice();
                    if (0 == openPrice.compareTo(BigDecimal.ZERO)
                            || 0 == preClosePrice.compareTo(BigDecimal.ZERO)) {
                        continue;
                    }
                    //当前价
                    BigDecimal currentPrice = sinaRealtimeDealInfoPo.getCurrentPrice();
                    //增幅
                    BigDecimal uptickRate = sinaRealtimeDealInfoPo.getUptickRate();
                    //波动
                    BigDecimal surgeRate = sinaRealtimeDealInfoPo.getSurgeRate();
                    //成交量
                    Long dealNum = sinaRealtimeDealInfoPo.getDealNum();
                    //成交金额
                    BigDecimal dealMoney = sinaRealtimeDealInfoPo.getDealMoney();

                    upRateLimit = stockToolService.limitRate(
                            new StockVo(sinaRealtimeDealInfoPo.getStockCode(), stockMarket),
                            sinaRealtimeDealInfoPo.getStockName()
                    );

                    //判断是否涨跌停
                    if (null != upRateLimit
                            && 0 >= preClosePrice.multiply(upRateLimit)
                            .setScale(2, RoundingMode.HALF_UP)
                            .compareTo(preClosePrice.subtract(currentPrice).abs())
                    ) {
                        if (0 < preClosePrice.compareTo(currentPrice)) {
                            downLimitStockCodeList.add(sinaRealtimeDealInfoPo.getStockCode());
                        } else {
                            upLimitStockCodeList.add(sinaRealtimeDealInfoPo.getStockCode());
                        }
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
            if (null != upLimitStockCodeList && !upLimitStockCodeList.isEmpty()) {
                stockRedisUtil.lPushAll(
                        cacheNamePre + upLimitListKey,
                        upLimitStockCodeList
                );
                stockRedisUtil.rename(
                        cacheNamePre + upLimitListKey,
                        upLimitListKey
                );
            }
            if (null != downLimitStockCodeList && !downLimitStockCodeList.isEmpty()) {
                stockRedisUtil.lPushAll(
                        cacheNamePre + downLimitListKey,
                        downLimitStockCodeList
                );
                stockRedisUtil.rename(
                        cacheNamePre + downLimitListKey,
                        downLimitListKey
                );
            }
        }
    }

    /**
     * 实时增幅统计
     */
    public void syncStockRealtimeUptickRateStatistics() {
        String schedule = "StockRealtimeRankSchedule:syncStockRealtimeUptickRateStatistics";
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            if (!realtimeDealScheduleCanRun(stockMarket, schedule)) {
                continue;
            }
            Map<String, Integer> uptickRateStatisticsMap = new LinkedHashMap<>();
            Map<String, List<Double>> scoreMap = new LinkedHashMap<String, List<Double>>() {{
                put("up", Arrays.asList(0.00001, 100000.0));
                put("down", Arrays.asList(-100.0, -0.00001));
                put("flat", Arrays.asList(-0.00001, 0.00001));
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
            Long upLimit = stockRedisUtil.lSize(stockRealtimeStockRankUpLimitList);
            uptickRateStatisticsMap.put("upLimit", null == upLimit ? 0 : upLimit.intValue());
            Long downLimit = stockRedisUtil.lSize(stockRealtimeStockRankDownLimitList);
            uptickRateStatisticsMap.put("downLimit", null == downLimit ? 0 : downLimit.intValue());
            stockRedisUtil.set(stockRealtimeStockUptickRateStatistics + ":" + stockMarket, uptickRateStatisticsMap);
        }
    }
}
