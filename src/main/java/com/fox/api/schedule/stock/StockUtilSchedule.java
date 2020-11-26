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
     * 新浪实时交易信息
     */
    @Autowired
    StockToolService stockToolService;
    /**
     * 港股股票信息类
     */
    @Autowired
    HKStockInfoApi hkStockInfoApi;
    /**
     * 股市交易日期数据操作类
     */
    @Autowired
    StockDealDateMapper stockDealDateMapper;

    /**
     * 更新股市近的3个交易日期
     */
    @LogShowTimeAnt
    public void syncStockMarketAroundDealDate() {
        String currentDate = DateUtil.getCurrentDate();
        StockDealDateEntity stockDealDateEntity = new StockDealDateEntity();
        stockDealDateEntity.setDt(currentDate);
        for (Integer stockMarket : StockConst.SM_ALL) {
            stockDealDateEntity.setStockMarket(stockMarket);
            if (StockConst.SM_A_LIST.contains(stockMarket)) {
                stockDealDateEntity.setStockMarket(StockConst.SM_A);
            }

            //更新上个交易日
            StockDealDateEntity preStockDealDateEntity = stockDealDateMapper.pre(stockDealDateEntity);
            if (null != preStockDealDateEntity) {
                stockRedisUtil.set(StockUtil.preDealDateCacheKey(stockMarket), preStockDealDateEntity.getDt());
            }
            //更新当前交易日
            StockDealDateEntity lastStockDealDateEntity = stockDealDateMapper.last(stockDealDateEntity);
            if (null != lastStockDealDateEntity) {
                stockRedisUtil.set(StockUtil.lastDealDateCacheKey(stockMarket), lastStockDealDateEntity.getDt());
            }
            //更新下一个交易日
            StockDealDateEntity nextStockDealDateEntity = stockDealDateMapper.next(stockDealDateEntity);
            if (null != nextStockDealDateEntity) {
                stockRedisUtil.set(StockUtil.nextDealDateCacheKey(stockMarket), nextStockDealDateEntity.getDt());
            }
        }
    }

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
