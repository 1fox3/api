package com.fox.api;

import com.fox.api.service.stock.StockInfoService;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {
    @Autowired
    StockInfoService stockInfoService;
    /**
     * 沪市测试股票（顶点软件）
     */
    public static final StockVo TEST_SH_STOCK = new StockVo("603383", StockConst.SM_SH);
    /**
     * 深市测试股票（同花顺）
     */
    public static final StockVo TEST_SZ_STOCK = new StockVo("300033", StockConst.SM_SZ);
    /**
     * 港股测试股票（腾讯控股）
     */
    public static final StockVo TEST_HK_STOCK =  new StockVo("00700", StockConst.SM_HK);

    @Test
    void contextLoads() {
        System.out.println(stockInfoService.getInfoFromStockExchange(
                TEST_SH_STOCK
        ));
    }
}
