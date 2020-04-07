package com.fox.api;

import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;

import java.text.SimpleDateFormat;
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

    @Value("${redis.stock.realtime.stock.rank.uptick-statistics}")
    protected String stockRealtimeStockUptickRateStatistics;

    @Autowired
    protected StockDealDayMapper stockDealDayMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
    }
}
