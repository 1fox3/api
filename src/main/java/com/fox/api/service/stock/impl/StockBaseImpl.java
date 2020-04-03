package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.property.stock.StockProperty;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;

import java.util.List;
import java.util.Map;

public class StockBaseImpl {
    @Autowired
    protected StockRedisUtil stockRedisUtil;

    @Autowired
    protected StockProperty stockProperty;

    @Value("${redis.stock.stock.hash}")
    protected String redisStockHash;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.stock.heat-list}")
    protected String redisStockHeatList;

    @Value("${redis.stock.realtime.stock.line.single}")
    protected String redisRealtimeStockLineSingle;

    @Value("${redis.stock.realtime.stock.rank.uptick}")
    protected String redisRealtimeRankUptickRateZSet;

    @Value("${redis.stock.realtime.stock.rank.surge}")
    protected String redisRealtimeRankSurgeRateZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.num}")
    protected String redisRealtimeRankDealNumZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.money}")
    protected String redisRealtimeRankDealMoneyZSet;

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
