package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class StockBaseImpl {
    @Autowired
    protected StockRedisUtil stockRedisUtil;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.stock.heat-list}")
    protected String redisStockHeatList;

    @Value("${redis.stock.realtime.stock.line.single}")
    protected String redisRealtimeStockLineSingle;

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
        return StockUtil.getNetsStockInfoMap(stockEntity);
    }
}
