package com.fox.api.util;

import com.fox.api.util.redis.StockRedisUtil;
import com.fox.spider.stock.constant.StockConst;

/**
 * 股票工具类
 *
 * @author lusongsong
 * @date 2020/3/30 11:26
 */
public class StockUtil {
    /**
     * 香港交易所token缓存key
     */
    public static String HK_STOCK_MARKET_TOKEN = "hkStockMarketToken";
    /**
     * 最新交易日期缓存key
     */
    private static String stockMarketLastDealDateCacheKey = "stockMarketLastDealDate";
    /**
     * 上个交易日缓存key
     */
    private static String stockMarketPreDealDateCacheKey = "stockMarketPreDealDate";
    /**
     * 下个交易日缓存key
     */
    private static String stockMarketNextDealDateCacheKey = "stockMarketNextDealDate";
    /**
     * 交易所交易状态缓存key
     */
    public static String SM_DEAL_STATUS = "stockMarketDealStatus";

    /**
     * 股票专用缓存
     *
     * @param key
     * @return
     */
    private static Object redisGet(String key) {
        StockRedisUtil stockRedisUtil = (StockRedisUtil) ApplicationContextUtil.getBean("stockRedisUtil");
        return stockRedisUtil.get(key);
    }

    /**
     * 获取最新交易日的缓存key
     *
     * @param stockMarket
     * @return
     */
    public static String lastDealDateCacheKey(Integer stockMarket) {
        return stockMarketLastDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取股市的最新交易日
     *
     * @param stockMarket
     * @return
     */
    public static String lastDealDate(Integer stockMarket) {
        String cacheKey = StockUtil.lastDealDateCacheKey(stockMarket);
        return (String) redisGet(cacheKey);
    }

    /**
     * 获取上个交易日的缓存key
     *
     * @param stockMarket
     * @return
     */
    public static String preDealDateCacheKey(Integer stockMarket) {
        return stockMarketPreDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取上个交易日
     *
     * @param stockMarket
     * @return
     */
    public static String preDealDate(Integer stockMarket) {
        String cacheKey = StockUtil.preDealDateCacheKey(stockMarket);
        return (String) redisGet(cacheKey);
    }

    /**
     * 获取下个交易日的缓存key
     *
     * @param stockMarket
     * @return
     */
    public static String nextDealDateCacheKey(Integer stockMarket) {
        return stockMarketNextDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取下个交易日
     *
     * @param stockMarket
     * @return
     */
    public static String nextDealDate(Integer stockMarket) {
        String cacheKey = StockUtil.nextDealDateCacheKey(stockMarket);
        return (String) redisGet(cacheKey);
    }

    /**
     * 判断今天是否为交易日
     *
     * @param stockMarket
     * @return
     */
    public static Boolean todayIsDealDate(Integer stockMarket) {
        String today = DateUtil.getCurrentDate();
        return today.equals(StockUtil.lastDealDate(stockMarket));
    }

    /**
     * 获取香港交易所token
     *
     * @return
     */
    public static String hkStockMarketToken() {
        return (String) redisGet(StockUtil.HK_STOCK_MARKET_TOKEN);
    }

    /**
     * 获取股市交易状态
     *
     * @param stockMarket
     * @return
     */
    public static Integer smDealStatus(Integer stockMarket) {
        if (null != stockMarket && StockConst.SM_ALL.contains(stockMarket)) {
            return (Integer) redisGet(SM_DEAL_STATUS + ":" + stockMarket);
        }
        return null;
    }
}
