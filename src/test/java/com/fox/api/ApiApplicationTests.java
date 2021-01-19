package com.fox.api;

import com.fox.api.service.stock.api.request.StockRealtimeDealInfoApiService;
import com.fox.api.service.stock.api.request.impl.StockApiServiceBaseImpl;
import com.fox.api.service.stock.api.request.impl.StockRealtimeDealInfoApiImpl;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {
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
    public static final StockVo TEST_HK_STOCK = new StockVo("00700", StockConst.SM_HK);
    public static final StockVo TEST_US_STOCK = new StockVo("00700", 4);

    @Autowired
    StockRealtimeDealInfoApiService stockRealtimeDealInfoApiService;

    @Test
    void contextLoads() {
        stockRealtimeDealInfoApiService.setChooseMethod(StockApiServiceBaseImpl.CHOOSE_METHOD_POLL);
        for (int i = 0; i < 5; i++) {
            System.out.println(stockRealtimeDealInfoApiService.realtimeDealInfo(TEST_SH_STOCK));
        }
    }
}
