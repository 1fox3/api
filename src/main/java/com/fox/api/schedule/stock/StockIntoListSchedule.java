package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
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
    /**
     * 清楚缓存中的股票信息，并重新填充
     */
    @LogShowTimeAnt
    public void clearStockCacheInfo() {
        this.stockRedisUtil.delete(this.redisStockHash);
        this.stockRedisUtil.delete(this.redisStockList);
        this.stockRedisUtil.delete(this.redisStockIdList);
        this.stockIntoList();
    }

    /**
     * 删除数据信息
     */
    @LogShowTimeAnt
    public void clearStockCacheData() {
        this.stockRedisUtil.delete(this.redisRealtimeStockInfoHash);
        this.stockRedisUtil.delete(this.redisRealtimeStockLineHash);
        this.stockRedisUtil.delete(this.redisRealtimeRankPriceZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankUptickRateZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankSurgeRateZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankDealNumZSet);
        this.stockRedisUtil.delete(this.redisRealtimeRankDealMoneyZSet);
        this.stockRedisUtil.delete(this.stockRealtimeStockUptickRateStatistics);
        this.stockIntoList();
    }

    /**
     * 每5分钟检查一次,防止缓存失效或者重启导致的缓存数据丢失
     */
    @LogShowTimeAnt
    public void stockIntoList() {
        int startId = 0;
        if (this.stockRedisUtil.hasKey(this.redisStockList) && 0 != this.stockRedisUtil.lSize(this.redisStockList)) {
            return;
        }
        List<StockEntity> stockEntityList;
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
}
