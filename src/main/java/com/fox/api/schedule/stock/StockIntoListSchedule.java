package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 将需要频繁处理的股票信息放入缓存列表中，方便以后使用，不用在查询数据库
 * @author lusongsong
 * @date 2020/3/27 13:57
 */
@Component
public class StockIntoListSchedule extends StockBaseSchedule {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 缓存key修改前缀
     */
    private String cacheNamePre = "pre";
    /**
     * 清楚缓存中的股票信息，并重新填充
     */
    @LogShowTimeAnt
    public void refreshStockCacheInfo() {
        try {
            if (isDealDate(StockConst.SM_A, DateUtil.getCurrentDate())) {
                stockIntoDBToRedis();
                clearStockCacheData();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 删除数据信息
     */
    @LogShowTimeAnt
    public void clearStockCacheData() {
        try {
            if (isDealDate(StockConst.SM_A, DateUtil.getCurrentDate())) {
                this.stockRedisUtil.delete(this.redisRealtimeStockInfoHash);
                this.stockRedisUtil.delete(this.redisRealtimeStockLineHash);
                this.stockRedisUtil.delete(this.redisRealtimeRankPriceZSet);
                this.stockRedisUtil.delete(this.redisRealtimeRankUptickRateZSet);
                this.stockRedisUtil.delete(this.redisRealtimeRankSurgeRateZSet);
                this.stockRedisUtil.delete(this.redisRealtimeRankDealNumZSet);
                this.stockRedisUtil.delete(this.redisRealtimeRankDealMoneyZSet);
                this.stockRedisUtil.delete(this.stockRealtimeStockUptickRateStatistics);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 每5分钟检查一次,防止缓存失效或者重启导致的缓存数据丢失
     */
    @LogShowTimeAnt
    public void stockIntoList() {
        if (this.stockRedisUtil.hasKey(this.redisStockList) && 0 != this.stockRedisUtil.lSize(this.redisStockList)) {
            return;
        }
        stockIntoDBToRedis();
    }

    /**
     * 数据从数据导入缓存
     */
    public void stockIntoDBToRedis() {
        List<StockEntity> stockEntityList;
        int startId = 0;
        String limit = "500";
        List<Integer> idList = new LinkedList<>();
        Map<String, StockEntity> stockEntityMap = new LinkedHashMap<>();
        for (Integer stockMarket : StockConst.SM_A_LIST) {
            while (true) {
                stockEntityList = stockMapper.getListByType(this.stockType, startId, stockMarket, limit);
                if (null == stockEntityList || 0 == stockEntityList.size()) {
                    break;
                }
                startId = stockEntityList.get(stockEntityList.size() - 1).getId();
                this.stockRedisUtil.lPushAll(cacheNamePre + this.redisStockList, stockEntityList);
                idList.clear();
                stockEntityMap.clear();
                for (StockEntity stockEntity : stockEntityList) {
                    idList.add(stockEntity.getId());
                    stockEntityMap.put(String.valueOf(stockEntity.getId()), stockEntity);
                }
                this.stockRedisUtil.hPutAll(cacheNamePre + this.redisStockHash, stockEntityMap);
                this.stockRedisUtil.lPushAll(cacheNamePre + this.redisStockIdList, idList);
            }
        }
        this.stockRedisUtil.rename(cacheNamePre + this.redisStockList, this.redisStockList);
        this.stockRedisUtil.rename(cacheNamePre + this.redisStockHash, this.redisStockHash);
        this.stockRedisUtil.rename(cacheNamePre + this.redisStockIdList, this.redisStockIdList);
    }
}
