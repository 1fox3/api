package com.fox.api.util;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Autowired
    private static StockRedisUtil stockRedisUtil;

    /**
     * 获取股票集市字符串
     * @return
     */
    public static String getStockMarketStr(Integer stockMarket) {
        if (2 == stockMarket) {
            return "sz";
        }
        if (3 == stockMarket) {
            return "hk";
        }
        return "sh";
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
        String lastDealDate = "";
        if (StockUtil.stockRedisUtil.hasKey(cacheKey)) {
            lastDealDate = (String)StockUtil.stockRedisUtil.get(cacheKey);
            if (!lastDealDate.equals("")) {
                return lastDealDate;
            }
        }
        return "";
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
}
