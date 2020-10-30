package com.fox.api;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockPriceDealNumDayMapper;
import com.fox.api.schedule.stock.StockPriceDealNumDaySchedule;
import com.fox.api.service.third.stock.sina.api.SinaDealRatio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {
    @Autowired
    StockPriceDealNumDaySchedule stockPriceDealNumDaySchedule;

    @Test
    void contextLoads() {
//        stockPriceDealNumDaySchedule.syncTotalPriceDealNumDayInfo();
        StockEntity stockEntity = new StockEntity();
        stockEntity.setStockCode("600000");
        stockEntity.setStockMarket(1);
        SinaDealRatio sinaDealRatio = new SinaDealRatio();
        System.out.println(sinaDealRatio.getDealRatio(stockEntity, "2020-10-29", "2020-10-29"));
    }
}
