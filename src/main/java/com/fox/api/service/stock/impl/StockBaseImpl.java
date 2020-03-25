package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class StockBaseImpl {
    @Autowired
    protected StockMapper stockMapper;

    /**
     * 获取股票信息
     * @param stockId
     * @return
     */
    protected StockEntity getStockEntity(int stockId) {
        return this.stockMapper.getById(stockId);
    }

    /**
     * 获取新浪股票代码
     * @param stockId
     * @return
     */
    protected String getSinaStockCode(int stockId) {
        StockEntity stockEntity = this.getStockEntity(stockId);
        return null == stockEntity ? "" : stockEntity.getSinaStockCode();
    }

    /**
     * 获取网易股票代码
     * @param stockId
     * @return
     */
    protected Map<String, String> getNetsStockInfoMap(int stockId) {
        StockEntity stockEntity = this.getStockEntity(stockId);
        Map<String, String> netsStockInfoMap = new HashMap<>();
        String netsStockCode = null == stockEntity ? "" : stockEntity.getNetsStockCode();
        String netsStockMarket = null == stockEntity ? "sh" : stockEntity.getStockMarketStr();
        String netsStockMarketPY = NetsStockBaseApi.getNetsStockMarketPY(netsStockMarket);
        netsStockInfoMap.put("netsStockMarketPY", netsStockMarketPY);
        netsStockInfoMap.put("netsStockCode", netsStockCode);
        return netsStockInfoMap;
    }
}
