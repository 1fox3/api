package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDateEntity;
import com.fox.api.dao.stock.mapper.StockDealDateMapper;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.hk.HKStockInfoApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.constant.StockMarketStatusConst;
import com.fox.spider.stock.service.StockToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 股票工具类任务，提供基本信息
 *
 * @author lusongsong
 * @date 2020/5/2 14:20
 */
@Component
public class StockUtilSchedule extends StockBaseSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 港股股票信息类
     */
    @Autowired
    HKStockInfoApi hkStockInfoApi;

    /**
     * 刷新香港交易所token
     */
    @LogShowTimeAnt
    public void hkStockMarketToken() {
        try {
            String token = hkStockInfoApi.apiToken();
            if (null != token && !token.isEmpty()) {
                stockRedisUtil.set(StockUtil.HK_STOCK_MARKET_TOKEN, token);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 同步股市交易状态
     */
    public void syncSMDealStatus() {
        for (Integer stockMarket : StockConst.SM_ALL) {
            int dealStatus = StockMarketStatusConst.REST;
            if (StockUtil.todayIsDealDate(stockMarket)) {
                dealStatus = StockMarketStatusConst.timeSMStatus(stockMarket);
            }
            stockRedisUtil.set(StockUtil.SM_DEAL_STATUS + ":" + stockMarket, dealStatus);
        }
    }
}
