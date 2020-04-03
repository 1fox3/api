package com.fox.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.property.stock.StockProperty;
import com.fox.api.schedule.stock.StockScanSchedule;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.service.stock.StockUtilService;
import com.fox.api.util.redis.StockRedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;

import java.util.*;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    private StockMapper stockMapper;

    @Value("${stock.type.stock.stock-type}")
    private int stockType;

    @Value("${stock.market.sh.stock-market}")
    private int shStockMarket;

    @Value("${stock.market.sz.stock-market}")
    private int szStockMarket;

    @Value("${redis.stock.realtime.stock.rank.uptick}")
    protected String redisRealtimeRankUptickRateZSet;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.stock.hash}")
    protected String redisStockHash;

    @Value("${redis.stock.stock.list}")
    protected String redisStockList;

    @Autowired
    private StockRedisUtil stockRedisUtil;

    @Autowired
    private StockUtilService stockUtilService;

    @Autowired
    private StockProperty stockProperty;

    @Autowired
    private StockScanSchedule stockScanSchedule;

    @Autowired
    private StockRealtimeService stockRealtimeService;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
    }
}
