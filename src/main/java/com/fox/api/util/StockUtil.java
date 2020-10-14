package com.fox.api.util;

import com.fox.api.constant.StockConst;
import com.fox.api.util.redis.StockRedisUtil;

/**
 * 股票工具类
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
     * 沪深交易起止时间
     */
    private static String shMorningDealStartTime = "09:30:00";
    private static String shMorningDealEndTime = "11:30:00";
    private static String shAfternoonDealStartTime = "13:00:00";
    private static String shAfternoonDealEndTime = "15:00:00";
    /**
     * 港交易起止时间
     */
    private static String hkMorningDealStartTime = "09:30:00";
    private static String hkMorningDealEndTime = "12:00:00";
    private static String hkAfternoonDealStartTime = "13:00:00";
    private static String hkAfternoonDealEndTime = "16:10:00";

    /**
     * 股票专用缓存
     * @param key
     * @return
     */
    private static String redisGet(String key) {
        String info = "";
        StockRedisUtil stockRedisUtil = (StockRedisUtil)ApplicationContextUtil.getBean("stockRedisUtil");
        if (stockRedisUtil.hasKey(key)) {
            info = (String)stockRedisUtil.get(key);
        }
        return null == info ? "" : info;
    }

    /**
     * 获取最新交易日的缓存key
     * @param stockMarket
     * @return
     */
    public static String getLastDealDateCacheKey(Integer stockMarket) {
        return stockMarketLastDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取股市的最新交易日
     * @param stockMarket
     * @return
     */
    public static String getLastDealDate(Integer stockMarket) {
        String cacheKey = StockUtil.getLastDealDateCacheKey(stockMarket);
        return redisGet(cacheKey);
    }

    /**
     * 获取上个交易日的缓存key
     * @param stockMarket
     * @return
     */
    public static String getPreDealDateCacheKey(Integer stockMarket) {
        return stockMarketPreDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取上个交易日
     * @param stockMarket
     * @return
     */
    public static String getPreDealDate(Integer stockMarket) {
        String cacheKey = StockUtil.getPreDealDateCacheKey(stockMarket);
        return redisGet(cacheKey);
    }

    /**
     * 获取下个交易日的缓存key
     * @param stockMarket
     * @return
     */
    public static String getNextDealDateCacheKey(Integer stockMarket) {
        return stockMarketNextDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取下个交易日
     * @param stockMarket
     * @return
     */
    public static String getNextDealDate(Integer stockMarket) {
        String cacheKey = StockUtil.getNextDealDateCacheKey(stockMarket);
        return redisGet(cacheKey);
    }

    /**
     * 判断今天是否为交易日
     * @param stockMarket
     * @return
     */
    public static Boolean todayIsDealDate(Integer stockMarket) {
        String today = DateUtil.getCurrentDate();
        return today.equals(StockUtil.getLastDealDate(stockMarket));
    }

    /**
     * 判定当前是否为交易时间
     * @param stockMarket
     * @return
     */
    public static Boolean isDealTime(Integer stockMarket) {
        if (!StockUtil.todayIsDealDate(stockMarket)) {
            return false;
        }

        String currentTime = DateUtil.getCurrentTime(DateUtil.TIME_FORMAT_2);

        if (StockConst.SM_SH.equals(stockMarket) || StockConst.SM_SZ.equals(stockMarket)) {
            //上午交易时间
            if ((currentTime.compareTo(shMorningDealStartTime) >= 0
                    && currentTime.compareTo(shMorningDealEndTime) <=0)) {
                return true;
            }

            //下午交易时间
            if (currentTime.compareTo(shAfternoonDealStartTime) >= 0
                    && currentTime.compareTo(shAfternoonDealEndTime) <=0) {
                return true;
            }
        }

        if (StockConst.SM_HK.equals(stockMarket)) {
            //上午交易时间
            if ((currentTime.compareTo(hkMorningDealStartTime) >= 0 && currentTime.compareTo(hkMorningDealEndTime) <=0)) {
                return true;
            }

            //下午交易时间
            if (currentTime.compareTo(hkAfternoonDealStartTime) >= 0 && currentTime.compareTo(hkAfternoonDealEndTime) <=0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取香港交易所token
     * @return
     */
    public static String hkStockMarketToken() {
        return redisGet(StockUtil.HK_STOCK_MARKET_TOKEN);
    }
}
