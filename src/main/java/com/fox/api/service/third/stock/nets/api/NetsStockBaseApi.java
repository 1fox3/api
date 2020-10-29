package com.fox.api.service.third.stock.nets.api;

import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;

import java.util.HashMap;
import java.util.Map;

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
//        put(StockConst.SM_HK, "hk");//港
    }};

    /**
     * 股票id对应的前缀
     */
    public static Map<Integer, String> stockMarketPreCodeMap = new HashMap<Integer, String>(){{
        put(StockConst.SM_SH, "0");//沪
        put(StockConst.SM_SZ, "1");//深
//        put(StockConst.SM_HK, "");//港
    }};

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
     * 获取股票编码对应的前缀
     * @param stockMarket
     * @return
     */
    public static String getNetsStockPreCode(Integer stockMarket) {
        return NetsStockBaseApi.stockMarketPreCodeMap.containsKey(stockMarket) ?
                NetsStockBaseApi.stockMarketPreCodeMap.get(stockMarket) : "0";
    }

    /**
     * 获取网易股票代码接口需要的参数
     * @param stockEntity
     * @return
     */
    public static Map<String, String> getNetsStockInfoMap(StockEntity stockEntity) {
        Map<String, String> netsStockInfoMap = new HashMap<>(2);
        String netsStockCode = null == stockEntity ? "" : NetsStockBaseApi.getNetsStockCode(stockEntity);
        String netsStockMarketPY = NetsStockBaseApi.getNetsStockMarketPY(stockEntity.getStockMarket());
        netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
        netsStockInfoMap.put("netsStockCode", netsStockCode);
        return netsStockInfoMap;
    }

    /**
     * 获取网易对应的股票编码
     * @param stockEntity
     * @return
     */
    public static String getNetsStockCode(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode() || stockEntity.getStockCode().isEmpty()
                || null == stockEntity.getStockMarket()) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(NetsStockBaseApi.getNetsStockPreCode(stockEntity.getStockMarket()));
        stringBuffer.append(stockEntity.getStockCode());
        return stringBuffer.toString();
    }

    /**
     * 获取文件存储路径
     * @param params
     * @param dataName
     * @return
     */
    protected String saveFilePath(Map<String, String> params, String dataName) {
        String netsStockCode = params.containsKey("netsStockCode") ? params.get("netsStockCode") : "";
        String netsStockMarketPY = params.containsKey("netsStockMarketPY") ? params.get("netsStockMarketPY") : "";
        //去掉首位补充字符
        netsStockCode = netsStockCode.substring(1);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("/nets/");
        stringBuffer.append(dataName);
        stringBuffer.append("/");
        stringBuffer.append(netsStockMarketPY);
        stringBuffer.append("/");
        stringBuffer.append(netsStockCode.replaceAll("(\\d{2})", "$1/"));
        stringBuffer.append("/");
        return stringBuffer.toString().replace("//", "/");
    }
}
