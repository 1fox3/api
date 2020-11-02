package com.fox.api.service.third.stock.sina.api;

import com.fox.api.constant.stock.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.http.HttpResponseDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新浪股票数据基类
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class SinaStockBaseApi {
    /**
     * 股票交易所对应的拼音
     */
    public static Map<Integer, String> stockMarketPYMap = new HashMap<Integer, String>() {{
        put(StockConst.SM_SH, "sh");//沪
        put(StockConst.SM_SZ, "sz");//深
        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 当日无交易的状态
     */
    public static List<String> noDealStatusList = Arrays.asList("-3", "03", "-2", "07");

    /**
     * 处理json字符串的key无双引号的问题
     *
     * @param jsonStr
     * @return
     */
    protected String handleJsonStr(String jsonStr) {
        return jsonStr.replaceAll("([a-zA-Z0-9_]+):", "\"$1\":");
    }

    /**
     * 获取股票集市对应的拼音
     *
     * @param stockMarket
     * @return
     */
    public static String getSinaStockMarketPY(Integer stockMarket) {
        return SinaStockBaseApi.stockMarketPYMap.containsKey(stockMarket) ?
                SinaStockBaseApi.stockMarketPYMap.get(stockMarket) : "hs";
    }

    /**
     * 获取新浪股票代码
     *
     * @param stockEntity
     * @return
     */
    public static String getSinaStockCode(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode() || stockEntity.getStockCode().isEmpty()
                || null == stockEntity.getStockMarket()) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SinaStockBaseApi.getSinaStockMarketPY(stockEntity.getStockMarket()));
        stringBuffer.append(stockEntity.getStockCode());
        return stringBuffer.toString();
    }

    /**
     * 获取文件存储路径
     *
     * @param stockEntity
     * @param dataName
     * @return
     */
    protected String saveFilePath(StockEntity stockEntity, String dataName) {
        String stockCode = "";
        if (null != stockEntity) {
            stockCode = null != stockEntity.getStockCode() ? stockEntity.getStockCode() : "";
        }
        Integer stockMarket = stockEntity.getStockMarket();
        String stockMarketPy = stockMarketPYMap.containsKey(stockMarket) ? stockMarketPYMap.get(stockMarket) : "";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/sina/");
        stringBuffer.append(dataName);
        stringBuffer.append("/");
        stringBuffer.append(stockMarketPy);
        stringBuffer.append("/");
        stringBuffer.append(stockCode.replaceAll("(\\d{2})", "$1/"));
        stringBuffer.append("/");
        return stringBuffer.toString().replace("//", "/");
    }

    /**
     * 判断是够已被拒绝
     *
     * @param httpResponse
     * @return
     */
    public static Boolean isForbidden(HttpResponseDto httpResponse) {
        return 456 == httpResponse.getCode();
    }

    /**
     * 处理拒绝
     */
    public static void handleForbidden() {
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
        }
    }
}
