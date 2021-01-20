package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.schedule.stock.handler.StockScheduleBatchHandler;
import com.fox.api.util.DateUtil;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 将需要频繁处理的股票信息放入缓存列表中，方便以后使用，不用在查询数据库
 *
 * @author lusongsong
 * @date 2020/3/27 13:57
 */
@Component
public class StockIntoListSchedule extends StockBaseSchedule implements StockScheduleBatchHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 股票代码缓存key
     */
    private String stockCodeListCacheKey = "";
    /**
     * 股票对象缓存key
     */
    private String stockVoListCacheKey = "";

    /**
     * A股缓存数据刷新
     */
    @LogShowTimeAnt
    public void stockCacheInfoRefresh(Integer stockMarket) {
        try {
            stockDBToRedis(stockMarket);
            clearStockCacheData(stockMarket);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 删除数据信息
     */
    @LogShowTimeAnt
    private void clearStockCacheData(Integer stockMarket) {
        try {
            if (isDealDate(stockMarket, DateUtil.getCurrentDate())) {
                this.stockRedisUtil.delete(this.redisRealtimeStockInfoHash + ":" + stockMarket);
                this.stockRedisUtil.delete(this.redisRealtimeRankPriceZSet + ":" + stockMarket);
                this.stockRedisUtil.delete(this.redisRealtimeRankUptickRateZSet + ":" + stockMarket);
                this.stockRedisUtil.delete(this.redisRealtimeRankSurgeRateZSet + ":" + stockMarket);
                this.stockRedisUtil.delete(this.redisRealtimeRankDealNumZSet + ":" + stockMarket);
                this.stockRedisUtil.delete(this.redisRealtimeRankDealMoneyZSet + ":" + stockMarket);
                this.stockRedisUtil.delete(this.stockRealtimeStockUptickRateStatistics + ":" + stockMarket);
                this.stockRedisUtil.delete(this.stockRealtimeStockStopStatistics + ":" + stockMarket);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 数据从数据导入缓存
     */
    @LogShowTimeAnt
    private void stockDBToRedis(Integer stockMarket) {
        if (!StockConst.SM_ALL.contains(stockMarket)) {
            return;
        }
        List<Integer> stockMarketList = Arrays.asList(stockMarket);
        if (StockConst.SM_A == stockMarket) {
            stockMarketList = StockConst.SM_A_LIST;
        }
        stockCodeListCacheKey = cacheNamePre + redisStockCodeList + ":" + stockMarket;
        stockVoListCacheKey = cacheNamePre + redisStockList + ":" + stockMarket;
        for (Integer sm : stockMarketList) {
            stockMarketBatchScan(sm, this);
        }
        this.stockRedisUtil.rename(stockCodeListCacheKey, stockCodeListCacheKey.replace(cacheNamePre, ""));
        this.stockRedisUtil.rename(stockVoListCacheKey, stockVoListCacheKey.replace(cacheNamePre, ""));
    }

    /**
     * 计划任务批量处理股票
     *
     * @param stockEntityList
     */
    @Override
    public void batchHandle(List<StockEntity> stockEntityList) {
        List<StockVo> stockVoList = StockBaseSchedule.stockListConvert(stockEntityList);
        if (null != stockVoList && !stockVoList.isEmpty()) {
            this.stockRedisUtil.lPushAll(stockVoListCacheKey, stockVoList);
            List<String> stockCodeList = new ArrayList<>(stockVoList.size());
            for (StockVo stockVo : stockVoList) {
                stockCodeList.add(stockVo.getStockCode());
            }
            this.stockRedisUtil.lPushAll(stockCodeListCacheKey, stockCodeList);
        }
    }
}
