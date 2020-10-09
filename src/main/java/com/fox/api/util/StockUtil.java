package com.fox.api.util;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.util.redis.StockRedisUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 股票工具类
 * @author lusongsong
 */
public class StockUtil {
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
     * 沪
     */
    private static Integer shStockMarketId = 1;
    private static String shStockMarket = "sh";
    /**
     * 深
     */
    private static Integer szStockMarketId = 2;
    private static String szStockMarket = "sz";
    /**
     * 港
     */
    private static Integer hkStockMarketId = 3;
    private static String hkStockMarket = "hk";

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
     * 获取股票集市字符串
     * @return
     */
    public static String getStockMarketStr(Integer stockMarket) {
        if (szStockMarketId.equals(stockMarket)) {
            return szStockMarket;
        }
        if (hkStockMarketId.equals(stockMarket)) {
            return hkStockMarket;
        }
        return shStockMarket;
    }

    /**
     * 获取网易股票代码接口需要的参数
     * @param stockEntity
     * @return
     */
    public static Map<String, String> getNetsStockInfoMap(StockEntity stockEntity) {
        Map<String, String> netsStockInfoMap = new HashMap<>(2);
        String netsStockCode = null == stockEntity ? "" : stockEntity.getNetsStockCode();
        String netsStockMarket = null == stockEntity ? "sh" : StockUtil.getStockMarketStr(stockEntity.getStockMarket());
        String netsStockMarketPY = NetsStockBaseApi.getNetsStockMarketPY(netsStockMarket);
        netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
        netsStockInfoMap.put("netsStockCode", netsStockCode);
        return netsStockInfoMap;
    }

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
     * 获取最新交易日的缓存key
     * @param stockMarket
     * @return
     */
    public static String getLastDealDateCacheKey(String stockMarket) {
        return stockMarketLastDealDateCacheKey + ":" + stockMarket;
    }

    /**
     * 获取股市的最新交易日
     * @param stockMarket
     * @return
     */
    public static String getLastDealDate(String stockMarket) {
        String cacheKey = StockUtil.getLastDealDateCacheKey(stockMarket);
        return redisGet(cacheKey);
    }

    /**
     * 判断今天是否为交易日
     * @param stockMarket
     * @return
     */
    public static Boolean todayIsDealDate(String stockMarket) {
        String today = DateUtil.getCurrentDate();
        return today.equals(StockUtil.getLastDealDate(stockMarket));
    }

    /**
     * 判定当前是否为交易时间
     * @param stockMarket
     * @return
     */
    public static Boolean isDealTime(String stockMarket) {
        if (!StockUtil.todayIsDealDate(stockMarket)) {
            return false;
        }

        String currentTime = DateUtil.getCurrentTime(DateUtil.TIME_FORMAT_2);

        if (shStockMarket.equals(stockMarket) || szStockMarket.equals(stockMarket)) {
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

        if (hkStockMarket.equals(stockMarket)) {
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
}
