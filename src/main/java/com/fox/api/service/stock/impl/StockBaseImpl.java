package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.*;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class StockBaseImpl {
    @Autowired
    protected StockRedisUtil stockRedisUtil;

    @Value("${redis.stock.stock.hash}")
    protected String redisStockHash;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.realtime.stock.line.single}")
    protected String redisRealtimeStockLineSingle;

    @Value("${redis.stock.realtime.stock.rank.price}")
    protected String redisRealtimeRankPriceZSet;

    @Value("${redis.stock.realtime.stock.rank.uptick}")
    protected String redisRealtimeRankUptickRateZSet;

    @Value("${redis.stock.realtime.stock.rank.surge}")
    protected String redisRealtimeRankSurgeRateZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.num}")
    protected String redisRealtimeRankDealNumZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.money}")
    protected String redisRealtimeRankDealMoneyZSet;

    @Value("${redis.stock.realtime.stock.rank.uptick-statistics}")
    protected String stockRealtimeStockUptickRateStatistics;

    @Autowired
    protected StockMapper stockMapper;

    @Autowired
    protected StockDealDayMapper stockDealDayMapper;

    @Autowired
    protected StockDealWeekMapper stockDealWeekMapper;

    @Autowired
    protected StockDealMonthMapper stockDealMonthMapper;

    @Autowired
    protected StockDealMinuteMapper stockDealMinuteMapper;

    @Autowired
    protected StockInfoMapper stockInfoMapper;

    /**
     * 获取股票信息
     * @param stockId
     * @return
     */
    protected StockEntity getStockEntity(int stockId) {
        return this.stockMapper.getById(stockId);
    }
}
