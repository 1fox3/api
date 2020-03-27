package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * 将需要频繁处理的股票信息放入缓存列表中，方便以后使用，不用在查询数据库
 */
@Component
public class StockIntoListSchedule {

    @Autowired
    private StockMapper stockMapper;

    @Value("${stock.type.stock.stock-type}")
    private int stockType;

    @Value("${stock.market.sh.stock-market}")
    private int shStockMarket;

    @Value("${stock.market.sz.stock-market}")
    private int szStockMarket;

    @Value("${redis.stock.stock-list}")
    private String stockRedisList;

    @Autowired
    private StockRedisUtil stockRedisUtil;

    /**
     * 每天凌晨1点中清除一次，并重新填充
     */
    @Scheduled(cron="0 0 1 * * ?")
    public void clearStockIntoList() {
        this.stockRedisUtil.delete(this.stockRedisList);
        this.stockIntoList();
    }

    /**
     * 每5分钟检查一次,防止缓存失效或者重启导致的缓存数据丢失
     */
    @Scheduled(cron="0 */5 * * * ?")
    public void stockIntoList() {
        int startId = 0;
        if (this.stockRedisUtil.hasKey(this.stockRedisList)) {
            Long listSize = this.stockRedisUtil.lSize(this.stockRedisList);
            StockEntity stockEntity = (StockEntity)this.stockRedisUtil.lIndex(
                    this.stockRedisList, listSize - Long.valueOf(1));
            startId = stockEntity.getId();
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
            this.stockRedisUtil.lPushAll(this.stockRedisList, stockEntityList);
        }
    }
}
