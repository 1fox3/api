package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 将需要频繁处理的股票信息放入缓存列表中，方便以后使用，不用在查询数据库
 */
@Component
public class StockIntoListSchedule extends StockBaseSchedule {
    /**
     * 每天凌晨1点中清除一次，并重新填充
     */
    @Scheduled(cron="0 0 1 * * ?")
    public void clearStockIntoList() {
        this.stockRedisUtil.delete(this.redisStockList);
        this.stockIntoList();
    }

    /**
     * 每5分钟检查一次,防止缓存失效或者重启导致的缓存数据丢失
     */
    @Scheduled(cron="0 */5 * * * 1-5")
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
        while (true) {
            stockEntityList = stockMapper.getListByType(this.stockType, startId, limit, stockMarketList);
            if (null == stockEntityList || 0 == stockEntityList.size()) {
                break;
            }
            startId = stockEntityList.get(stockEntityList.size() - 1).getId();
            this.stockRedisUtil.lPushAll(this.redisStockList, stockEntityList);
        }
    }
}
