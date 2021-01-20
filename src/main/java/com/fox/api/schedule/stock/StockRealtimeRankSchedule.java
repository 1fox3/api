package com.fox.api.schedule.stock;

import com.fox.api.entity.po.stock.api.StockRealtimeDealInfoPo;
import com.fox.api.schedule.stock.handler.StockScheduleCacheBatchCodeHandler;
import com.fox.spider.stock.constant.StockConst;
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
public class StockRealtimeRankSchedule extends StockBaseSchedule implements StockScheduleCacheBatchCodeHandler {
    /**
     * 股票最新交易日交易信息缓存key
     */
    private String stockRealtimeDealInfoCacheHashKey = "";
    /**
     * 股票最新交易日价格缓存key
     */
    private String stockRealtimeRankPriceCacheZSetKey = "";
    /**
     * 股票最新交易日增幅缓存key
     */
    private String stockRealtimeRankUptickRateCacheZSetKey = "";
    /**
     * 股票最新交易日波动缓存key
     */
    private String stockRealtimeRankSurgeRateCacheZSetKey = "";
    /**
     * 股票最新交易日成交量缓存key
     */
    private String stockRealtimeRankDealNumCacheZSetKey = "";
    /**
     * 股票最新交易日成交额缓存key
     */
    private String stockRealtimeRankDealMoneyCacheZSetKey = "";
    /**
     * 股票最新交易日涨停缓存key
     */
    private String stockRealtimeUpLimitCacheListKey = "";
    /**
     * 股票最新交易日跌停缓存key
     */
    private String stockRealtimeDownLimitCacheListKey = "";
    /**
     * 股票最新交易日涨停股票代码列表
     */
    private List<String> upLimitStockCodeList = new ArrayList<>();
    /**
     * 股票最新交易日跌停股票代码列表
     */
    private List<String> downLimitStockCodeList = new ArrayList<>();
    /**
     * 股票工具类
     */
    @Autowired
    StockToolService stockToolService;

    /**
     * 排行统计
     */
    public void syncStockRealtimeRank() {
        String schedule = "StockRealtimeRankSchedule:syncStockRealtimeRank";
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            if (!realtimeDealScheduleCanRun(stockMarket, schedule)) {
                continue;
            }
            stockRealtimeDealInfoCacheHashKey = redisRealtimeStockInfoHash + ":" + stockMarket;
            stockRealtimeRankPriceCacheZSetKey = redisRealtimeRankPriceZSet + ":" + stockMarket;
            stockRealtimeRankUptickRateCacheZSetKey = redisRealtimeRankUptickRateZSet + ":" + stockMarket;
            stockRealtimeRankSurgeRateCacheZSetKey = redisRealtimeRankSurgeRateZSet + ":" + stockMarket;
            stockRealtimeRankDealNumCacheZSetKey = redisRealtimeRankDealNumZSet + ":" + stockMarket;
            stockRealtimeRankDealMoneyCacheZSetKey = redisRealtimeRankDealMoneyZSet + ":" + stockMarket;
            stockRealtimeUpLimitCacheListKey = stockRealtimeStockRankUpLimitList + ":" + stockMarket;
            stockRealtimeDownLimitCacheListKey = stockRealtimeStockRankDownLimitList + ":" + stockMarket;

            stockMarketCacheBatchCodeScan(stockMarket, this);

            if (null != upLimitStockCodeList && !upLimitStockCodeList.isEmpty()) {
                stockRedisUtil.lPushAll(
                        cacheNamePre + stockRealtimeUpLimitCacheListKey,
                        upLimitStockCodeList
                );
                stockRedisUtil.rename(
                        cacheNamePre + stockRealtimeUpLimitCacheListKey,
                        stockRealtimeUpLimitCacheListKey
                );
            }
            if (null != downLimitStockCodeList && !downLimitStockCodeList.isEmpty()) {
                stockRedisUtil.lPushAll(
                        cacheNamePre + stockRealtimeDownLimitCacheListKey,
                        downLimitStockCodeList
                );
                stockRedisUtil.rename(
                        cacheNamePre + stockRealtimeDownLimitCacheListKey,
                        stockRealtimeDownLimitCacheListKey
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
            Long upLimit = stockRedisUtil.lSize(stockRealtimeStockRankUpLimitList + ":" + stockMarket);
            uptickRateStatisticsMap.put("upLimit", null == upLimit ? 0 : upLimit.intValue());
            Long downLimit = stockRedisUtil.lSize(stockRealtimeStockRankDownLimitList + ":" + stockMarket);
            uptickRateStatisticsMap.put("downLimit", null == downLimit ? 0 : downLimit.intValue());
            stockRedisUtil.set(stockRealtimeStockUptickRateStatistics + ":" + stockMarket, uptickRateStatisticsMap);
        }
    }

    /**
     * 计划任务批量处理股票
     *
     * @param stockCodeList
     */
    @Override
    public void cacheBatchCodeHandle(List<String> stockCodeList) {
        if (null == stockCodeList || stockCodeList.isEmpty()) {
            return;
        }

        List<StockRealtimeDealInfoPo> stockRealtimeDealInfoPoList = (List<StockRealtimeDealInfoPo>) (List) stockRedisUtil.hMultiGet(
                stockRealtimeDealInfoCacheHashKey,
                stockCodeList
        );
        Set<DefaultTypedTuple> priceSet = new HashSet<>();
        Set<DefaultTypedTuple> uptickRateSet = new HashSet<>();
        Set<DefaultTypedTuple> surgeRateSet = new HashSet<>();
        Set<DefaultTypedTuple> dealNumSet = new HashSet<>();
        Set<DefaultTypedTuple> dealMoneySet = new HashSet<>();
        BigDecimal upRateLimit = null;
        for (StockRealtimeDealInfoPo stockRealtimeDealInfoPo : stockRealtimeDealInfoPoList) {
            if (null == stockRealtimeDealInfoPo) {
                continue;
            }
            String stockCode = stockRealtimeDealInfoPo.getStockCode();

            //今日开盘价
            BigDecimal openPrice = stockRealtimeDealInfoPo.getOpenPrice();
            //上个交易日收盘价
            BigDecimal preClosePrice = stockRealtimeDealInfoPo.getPreClosePrice();
            if (null == openPrice || 0 == openPrice.compareTo(BigDecimal.ZERO)
                    || null == preClosePrice || 0 == preClosePrice.compareTo(BigDecimal.ZERO)) {
                continue;
            }
            //当前价
            BigDecimal currentPrice = stockRealtimeDealInfoPo.getCurrentPrice();
            if (null != currentPrice) {
                priceSet.add(new DefaultTypedTuple(stockCode, currentPrice.doubleValue()));
                //增幅
                BigDecimal uptickRate = currentPrice.subtract(preClosePrice).multiply(new BigDecimal(100)).divide(preClosePrice, 2, RoundingMode.HALF_UP);
                if (null != uptickRate) {
                    uptickRateSet.add(new DefaultTypedTuple(stockCode, uptickRate.doubleValue()));
                }
            }
            //波动
            if (null != stockRealtimeDealInfoPo.getHighestPrice()
                    && 0 != stockRealtimeDealInfoPo.getHighestPrice().compareTo(BigDecimal.ZERO)
                    && null != stockRealtimeDealInfoPo.getLowestPrice()
                    && 0 != stockRealtimeDealInfoPo.getLowestPrice().compareTo(BigDecimal.ZERO)) {
                BigDecimal surgeRate = stockRealtimeDealInfoPo.getHighestPrice()
                        .subtract(stockRealtimeDealInfoPo.getLowestPrice())
                        .multiply(new BigDecimal(100))
                        .divide(preClosePrice, 2, RoundingMode.HALF_UP);
                if (null != surgeRate) {
                    surgeRateSet.add(new DefaultTypedTuple(stockCode, surgeRate.doubleValue()));
                }
            }
            //成交量
            Long dealNum = stockRealtimeDealInfoPo.getDealNum();
            if (null != dealNum) {
                dealNumSet.add(new DefaultTypedTuple(stockCode, dealNum.doubleValue()));
            }
            //成交金额
            BigDecimal dealMoney = stockRealtimeDealInfoPo.getDealMoney();
            if (null != dealMoney) {
                dealMoneySet.add(new DefaultTypedTuple(stockCode, dealMoney.doubleValue()));
            }
            //涨跌停
            upRateLimit = stockToolService.limitRate(
                    new StockVo(stockRealtimeDealInfoPo.getStockCode(), stockRealtimeDealInfoPo.getStockMarket()),
                    stockRealtimeDealInfoPo.getStockName()
            );
            //判断是否涨跌停
            if (null != upRateLimit
                    && 0 >= preClosePrice.multiply(upRateLimit)
                    .setScale(2, RoundingMode.HALF_UP)
                    .compareTo(preClosePrice.subtract(currentPrice).abs())
            ) {
                if (0 < preClosePrice.compareTo(currentPrice)) {
                    downLimitStockCodeList.add(stockCode);
                } else {
                    upLimitStockCodeList.add(stockCode);
                }
            }
        }
        if (!priceSet.isEmpty()) {
            stockRedisUtil.zAdd(stockRealtimeRankPriceCacheZSetKey, priceSet);
        }
        if (!uptickRateSet.isEmpty()) {
            stockRedisUtil.zAdd(stockRealtimeRankUptickRateCacheZSetKey, uptickRateSet);
        }
        if (!surgeRateSet.isEmpty()) {
            stockRedisUtil.zAdd(stockRealtimeRankSurgeRateCacheZSetKey, surgeRateSet);
        }
        if (!dealNumSet.isEmpty()) {
            stockRedisUtil.zAdd(stockRealtimeRankDealNumCacheZSetKey, dealNumSet);
        }
        if (!dealMoneySet.isEmpty()) {
            stockRedisUtil.zAdd(stockRealtimeRankDealMoneyCacheZSetKey, dealMoneySet);
        }
    }
}
