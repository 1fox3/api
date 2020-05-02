package com.fox.api;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.dao.quartz.mapper.QuartzJobMapper;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.schedule.TestJob;
import com.fox.api.service.quartz.QuartzJobService;
import com.fox.api.service.quartz.QuartzService;
import com.fox.api.service.stock.StockInfoService;
import com.fox.api.service.stock.StockRealtimeRankService;
import com.fox.api.util.ApplicationContextUtil;
import com.fox.api.util.redis.StockRedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

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

    @Autowired
    private StockInfoService stockInfoService;

    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private StockRealtimeRankService stockRealtimeRankService;

    @Autowired
    private QuartzJobMapper quartzJobMapper;

    @Autowired
    private QuartzJobService quartzJobService;

    @Autowired
    private TestJob testJob;
    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() throws Exception {
    }
}
