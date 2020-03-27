package com.fox.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.util.redis.StockRedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

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

    @Value("${redis.stock.stock-list}")
    private String stockRedisList;

    @Autowired
    private StockRedisUtil stockRedisUtil;

    @Test
    void contextLoads() {
    }

    @Test
    void redisTest() {
        int startId = 0;
        if (this.stockRedisUtil.hasKey(this.stockRedisList)) {
            Long listSize = this.stockRedisUtil.lSize(this.stockRedisList);
            StockEntity stockEntity = (StockEntity)this.stockRedisUtil.lIndex(
                    this.stockRedisList, listSize - Long.valueOf(1));
            startId = stockEntity.getId();
        }
        List<StockEntity> stockEntityList;
        String limit = "500";
        List<Integer> stockMarketList = new LinkedList<>();
        stockMarketList.add(this.shStockMarket);
        stockMarketList.add(this.szStockMarket);
        while (true) {
            stockEntityList = stockMapper.getListByType(this.stockType, startId, limit, stockMarketList);
            if (null == stockEntityList || 0 == stockEntityList.size()) {
                break;
            }
            startId = stockEntityList.get(stockEntityList.size() - 1).getId();
            this.stockRedisUtil.lPushAll(this.stockRedisList, stockEntityList);
        }
    }
}
