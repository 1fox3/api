package com.fox.api.util;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;

import java.util.HashMap;
import java.util.Map;

/**
 * 股票工具类
 */
public class StockUtil {

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
        Map<String, String> netsStockInfoMap = new HashMap<>();
        String netsStockCode = null == stockEntity ? "" : stockEntity.getNetsStockCode();
        String netsStockMarket = null == stockEntity ? "sh" : StockUtil.getStockMarketStr(stockEntity.getStockMarket());
        String netsStockMarketPY = NetsStockBaseApi.getNetsStockMarketPY(netsStockMarket);
        netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
        netsStockInfoMap.put("netsStockCode", netsStockCode);
        return netsStockInfoMap;
    }
}
