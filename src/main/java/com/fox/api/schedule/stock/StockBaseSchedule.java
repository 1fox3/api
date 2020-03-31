package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class StockBaseSchedule {
    @Autowired
    protected StockMapper stockMapper;

    @Value("${stock.type.stock.stock-type}")
    protected int stockType;

    @Value("${stock.market.sh.stock-market}")
    protected int shStockMarket;

    @Value("${stock.market.sz.stock-market}")
    protected int szStockMarket;

    @Value("${redis.stock.stock.list}")
    protected String redisStockList;

    @Value("${redis.stock.stock.hash}")
    protected String redisStockHash;

    @Value("${redis.stock.stock.id-list}")
    protected String redisStockIdList;

    @Value("${redis.stock.stock.heat-list}")
    protected String redisStockHeatList;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.realtime.stock.line.hash}")
    protected String redisRealtimeStockLineHash;

    @Value("${redis.stock.realtime.stock.rank.uptick}")
    protected String redisRealtimeRankUptickRateZSet;

    @Value("${redis.stock.realtime.stock.rank.surge}")
    protected String redisRealtimeRankSurgeRateZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.num}")
    protected String redisRealtimeRankDealNumZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.money}")
    protected String redisRealtimeRankDealMoneyZSet;

    @Autowired
    protected StockRedisUtil stockRedisUtil;
}
