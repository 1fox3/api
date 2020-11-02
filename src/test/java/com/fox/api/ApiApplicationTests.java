package com.fox.api;

import com.fox.api.schedule.stock.StockDealMinuteSchedule;
import com.fox.api.schedule.stock.StockPriceDealNumDaySchedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    StockPriceDealNumDaySchedule stockPriceDealNumDaySchedule;

    @Autowired
    StockDealMinuteSchedule stockDealMinuteSchedule;

    @Test
    void contextLoads() {
    }
}
