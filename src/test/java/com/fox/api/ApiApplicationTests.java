package com.fox.api;

import com.fox.api.model.stock.mapper.StockMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    private StockMapper stockStockMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testMapper() {
        System.out.println(stockStockMapper.getById(500));
    }
}
