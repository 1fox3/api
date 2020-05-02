package com.fox.api.service.third.stock.nets.api;

import java.util.*;

/**
 * 网易股票接口基类
 * @author lusongsong
 */
public class NetsStockBaseApi {
    /**
     * 股市对应的拼音
     */
    public static Map<String, String> stockMarketPYMap = new HashMap<String, String>(){{
       put("sh", "hs");//沪
       put("sz", "hs");//深
       put("hk", "hk");//港
    }};

    /**
     * 股票代码前缀
     */
    public static List<String> stockCodePrefix = new LinkedList<>(Arrays.asList("1", "0", ""));

    public static String getNetsStockMarketPY(String stockMarket) {
        return NetsStockBaseApi.stockMarketPYMap.containsKey(stockMarket) ?
                NetsStockBaseApi.stockMarketPYMap.get(stockMarket) : "hs";
    }
}
