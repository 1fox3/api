package com.fox.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
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

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
        System.out.println(this.stockRedisUtil.zSize(this.redisRealtimeRankUptickRateZSet));
        Set<Object> downSet = this.stockRedisUtil.zReverseRangeByScore(this.redisRealtimeRankUptickRateZSet, (double)-1.0, (double)-0.00001);
//        System.out.println(downSet);
        System.out.println(downSet.size());
        Set<Object> zeroSet = this.stockRedisUtil.zReverseRangeByScore(this.redisRealtimeRankUptickRateZSet, (double)-0.00001, (double)0.00001);
//        System.out.println(zeroSet);
        System.out.println(zeroSet.size());
        Set<Object> upSet = this.stockRedisUtil.zReverseRangeByScore(this.redisRealtimeRankUptickRateZSet, (double)0.00001, (double)1.0);
//        System.out.println(upSet);
        System.out.println(upSet.size());
//        Set<Object> set = this.stockRedisUtil.zReverseRangeWithScores(this.redisRealtimeRankUptickRateZSet, (long)0, (long)10);
//        Map<Integer, Double> scoreMap = new HashMap<>();
//        List list = new LinkedList();
//        for(Object object : set) {
//            Integer value = (Integer) ((DefaultTypedTuple)object).getValue();
//            Double score = ((DefaultTypedTuple)object).getScore();
//            list.add(value.toString());
//            scoreMap.put(value, score);
//        }
//
//        List<Object> stockEntityList = this.stockRedisUtil.hMultiGet(this.redisStockHash, list);
//        for (Object stockEntity : stockEntityList) {
//            stockEntity = (StockEntity)stockEntity;
//            System.out.println(((StockEntity) stockEntity).getId()
//                    + "\t" + ((StockEntity) stockEntity).getStockCode()
//                    + "\t" + ((StockEntity) stockEntity).getStockName()
//                    + "\t" + scoreMap.get(((StockEntity) stockEntity).getId())
//            );
//        }
    }
}
