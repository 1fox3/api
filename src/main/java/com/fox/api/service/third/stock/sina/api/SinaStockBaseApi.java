package com.fox.api.service.third.stock.sina.api;

import java.util.HashMap;
import java.util.Map;

public class SinaStockBaseApi {
    //股市对应的拼音
    public static Map<String, String> stockMarketPYMap = new HashMap<String, String>(){{
        put("sh", "sh");//沪
        put("sz", "sz");//深
        put("hk", "hk");//港
    }};

    /**
     * 处理json字符串的key无双引号的问题
     * @param jsonStr
     * @return
     */
    protected String handleJsonStr(String jsonStr) {
        return jsonStr.replaceAll("([a-zA-Z0-9_]+):", "\"$1\":");
    }
}
