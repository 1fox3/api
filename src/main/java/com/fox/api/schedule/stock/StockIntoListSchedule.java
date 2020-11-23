package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.util.DateUtil;
import com.fox.spider.stock.constant.StockConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 将需要频繁处理的股票信息放入缓存列表中，方便以后使用，不用在查询数据库
 *
 * @author lusongsong
 * @date 2020/3/27 13:57
 */
@Component
public class StockIntoListSchedule extends StockBaseSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 缓存key修改前缀
     */
    private String cacheNamePre = "pre";

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
            if (isDealDate(StockConst.SM_A, DateUtil.getCurrentDate())) {
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
        List<StockEntity> stockEntityList;
        Integer startId = 0;
        Integer limit = 300;
        Map<String, StockEntity> stockEntityMap = new LinkedHashMap<>();
        List<String> codeList = new ArrayList<>(limit);
        String idCacheKey = cacheNamePre + redisStockCodeList + ":" + stockMarket;
        String listCacheKey = cacheNamePre + redisStockList + ":" + stockMarket;
        String hashCacheKey = cacheNamePre + redisStockHash + ":" + stockMarket;
        for (Integer sm : stockMarketList) {
            while (true) {
                stockEntityList = stockMapper.getListByType(this.stockType, startId, sm, limit.toString());
                if (null == stockEntityList || stockEntityList.isEmpty()) {
                    break;
                }
                startId = stockEntityList.get(stockEntityList.size() - 1).getId();
                this.stockRedisUtil.lPushAll(listCacheKey, stockEntityList);
                stockEntityMap.clear();
                codeList.clear();
                for (StockEntity stockEntity : stockEntityList) {
                    stockEntityMap.put(String.valueOf(stockEntity.getId()), stockEntity);
                    codeList.add(stockEntity.getStockCode());
                }
                this.stockRedisUtil.lPushAll(idCacheKey, codeList);
                this.stockRedisUtil.hPutAll(hashCacheKey, stockEntityMap);
            }
        }
        this.stockRedisUtil.rename(idCacheKey, idCacheKey.replace(cacheNamePre, ""));
        this.stockRedisUtil.rename(listCacheKey, listCacheKey.replace(cacheNamePre, ""));
        this.stockRedisUtil.rename(hashCacheKey, hashCacheKey.replace(cacheNamePre, ""));
    }
}
