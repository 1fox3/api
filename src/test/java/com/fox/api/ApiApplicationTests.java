package com.fox.api;

import com.fox.api.schedule.stock.StockPriceDealNumDaySchedule;
import com.fox.api.service.third.stock.sina.api.SinaDealRatio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    StockPriceDealNumDaySchedule stockPriceDealNumDaySchedule;

    @Test
    void contextLoads() throws IOException {
    }
}
