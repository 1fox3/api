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

    @Value("${redis.stock.stock.heat-list}")
    protected String redisStockHeatList;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.realtime.stock.line.hash}")
    protected String redisRealtimeStockLineHash;

    @Autowired
    protected StockRedisUtil stockRedisUtil;
}
