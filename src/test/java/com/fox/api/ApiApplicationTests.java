package com.fox.api;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.dao.quartz.mapper.QuartzJobMapper;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.schedule.TestJob;
import com.fox.api.service.quartz.QuartzJobService;
import com.fox.api.service.stock.StockInfoService;
import com.fox.api.service.stock.StockRealtimeRankService;
import com.fox.api.util.redis.StockRedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

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

    //@Test
    void redisTest() {
        QuartzJobEntity quartzJobEntity = new QuartzJobEntity();
        quartzJobEntity.setJobKey("1");
        quartzJobEntity.setJobName("测试任务1");
        quartzJobEntity.setJobStatus("normal");
        quartzJobEntity.setJobGroup("test");
        quartzJobEntity.setCronExpr("* * * * * ?");
        quartzJobEntity.setNote("随便说点什么吧");
        quartzJobEntity.setBeanName("testJob");
        quartzJobEntity.setMethodName("execute");
        quartzJobService.insert(quartzJobEntity);
        Integer jobId = quartzJobEntity.getId();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        quartzJobService.startJob(jobId);
        System.out.println("111111111111111111");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        quartzJobService.pauseJob(jobId);
        System.out.println("222222222222222222");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        quartzJobService.resumeJob(jobId);
        System.out.println("3333333333333333333");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        quartzJobService.deleteJob(jobId);
        System.out.println("444444444444444444444444");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
