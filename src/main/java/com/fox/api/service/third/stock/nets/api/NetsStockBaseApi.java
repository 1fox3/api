package com.fox.api.service.third.stock.nets.api;

import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;

import java.util.*;

/**
 * 网易股票接口基类
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class NetsStockBaseApi {
    /**
     * 股市对应的拼音
     */
    public static Map<Integer, String> stockMarketPYMap = new HashMap<Integer, String>(){{
        put(StockConst.SM_SH, "hs");//沪
        put(StockConst.SM_SZ, "hs");//深
        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 股票代码前缀
     */
    public static List<String> stockCodePrefix = new LinkedList<>(Arrays.asList("1", "0", ""));

    /**
     * 获取股票集市对应的拼音
     * @param stockMarket
     * @return
     */
    public static String getNetsStockMarketPY(Integer stockMarket) {
        return NetsStockBaseApi.stockMarketPYMap.containsKey(stockMarket) ?
                NetsStockBaseApi.stockMarketPYMap.get(stockMarket) : "hs";
    }

    /**
     * 获取网易股票代码接口需要的参数
     * @param stockEntity
     * @return
     */
    public static Map<String, String> getNetsStockInfoMap(StockEntity stockEntity) {
        Map<String, String> netsStockInfoMap = new HashMap<>(2);
        String netsStockCode = null == stockEntity ? "" : stockEntity.getNetsStockCode();
        String netsStockMarketPY = NetsStockBaseApi.getNetsStockMarketPY(stockEntity.getStockMarket());
        netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
        netsStockInfoMap.put("netsStockCode", netsStockCode);
        return netsStockInfoMap;
    }
}
