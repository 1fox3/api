package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 将需要频繁处理的股票信息放入缓存列表中，方便以后使用，不用在查询数据库
 * @author lusongsong
 */
@Component
public class StockIntoListSchedule extends StockBaseSchedule {
    @LogShowTimeAnt
//    @Scheduled(cron="0 0 8 * * ?")
    /**
     * 每天早晨8点中清除一次，并重新填充
     */
    public void clearStockIntoList() {
        if (!this.todayIsDealDate()) {
            return;
        }
        this.stockRedisUtil.delete(this.redisStockHash);
        this.stockRedisUtil.delete(this.redisStockList);
        this.stockRedisUtil.delete(this.redisStockIdList);
        this.stockIntoList();
    }

    @LogShowTimeAnt
//    @Scheduled(cron="0 0 9 * * ?")
    /**
     * 每天早晨9点删除数据信息
     */
    public void clearStockDealList() {
        if (!this.todayIsDealDate()) {
            return;
        }
        this.stockRedisUtil.delete(this.redisRealtimeStockInfoHash);
        this.stockRedisUtil.delete(this.redisRealtimeStockLineHash);
        this.stockRedisUtil.delete(this.redisRealtimeRankUptickRateZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankSurgeRateZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankDealNumZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankDealMoneyZSet);
        this.stockRedisUtil.delete(this.stockRealtimeStockUptickRateStatistics);
        this.stockIntoList();
    }


    @LogShowTimeAnt
//    @Scheduled(cron="0 */5 * * * 1-5")
    /**
     * 每5分钟检查一次,防止缓存失效或者重启导致的缓存数据丢失
     */
    public void stockIntoList() {
        int startId = 0;
        if (this.stockRedisUtil.hasKey(this.redisStockList)) {
            Long listSize = this.stockRedisUtil.lSize(this.redisStockList);
            StockEntity stockEntity = (StockEntity)this.stockRedisUtil.lIndex(
                    this.redisStockList, listSize - Long.valueOf(1));
            if (null != stockEntity) {
                startId = stockEntity.getId();
            }
        }
        List<StockEntity> stockEntityList;
        String limit = "500";
        List<Integer> stockMarketList = new LinkedList<>();
        stockMarketList.add(this.shStockMarket);
        stockMarketList.add(this.szStockMarket);
        List<Integer> idList = new LinkedList<>();
        Map<String, StockEntity> stockEntityMap = new LinkedHashMap<>();
        while (true) {
            stockEntityList = stockMapper.getListByType(this.stockType, startId, limit, stockMarketList);
            if (null == stockEntityList || 0 == stockEntityList.size()) {
                break;
            }
            startId = stockEntityList.get(stockEntityList.size() - 1).getId();
            this.stockRedisUtil.lPushAll(this.redisStockList, stockEntityList);
            idList.clear();
            stockEntityMap.clear();
            for (StockEntity stockEntity : stockEntityList) {
                idList.add(stockEntity.getId());
                stockEntityMap.put(String.valueOf(stockEntity.getId()), stockEntity);
            }
            this.stockRedisUtil.hPutAll(this.redisStockHash, stockEntityMap);
            this.stockRedisUtil.lPushAll(this.redisStockIdList, idList);
        }
    }
}
