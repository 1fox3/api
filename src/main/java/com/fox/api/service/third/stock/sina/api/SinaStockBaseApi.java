package com.fox.api.service.third.stock.sina.api;

import com.fox.api.constant.StockConst;

import java.util.HashMap;
import java.util.Map;

/**
 * 新浪股票数据基类
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class SinaStockBaseApi {
    /**
     * 股票交易所对应的拼音
     */
    public static Map<Integer, String> stockMarketPYMap = new HashMap<Integer, String>(){{
        put(StockConst.SM_SH, "sh");//沪
        put(StockConst.SM_SZ, "sz");//深
        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 处理json字符串的key无双引号的问题
     * @param jsonStr
     * @return
     */
    protected String handleJsonStr(String jsonStr) {
        return jsonStr.replaceAll("([a-zA-Z0-9_]+):", "\"$1\":");
    }

    /**
     * 获取股票集市对应的拼音
     * @param stockMarket
     * @return
     */
    public static String getSinaStockMarketPY(Integer stockMarket) {
        return SinaStockBaseApi.stockMarketPYMap.containsKey(stockMarket) ?
                SinaStockBaseApi.stockMarketPYMap.get(stockMarket) : "hs";
    }
}
