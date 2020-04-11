package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockInfoEntity;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.service.stock.StockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockInfoSchedule extends StockBaseSchedule {
    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private StockInfoService stockInfoService;

    /**
     * 同步所有的按天交易信息数据
     */
    @Scheduled(cron="0 0 1 * * 1")
    public void stockInfo() {
        Integer onceLimit = 200;
        Integer stockId = 0;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);
        for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(this.redisStockList, i, i + onceLimit - 1);
            if (null == stockEntityList || 0 >= stockEntityList.size()) {
                continue;
            }
            for (Object object : stockEntityList) {
                try{
                    stockId = ((StockEntity)object).getId();
                    StockInfoEntity shStockInfoEntity = stockInfoService.getInfoFromStockExchange(stockId);
                    StockInfoEntity dbSHStockInfoEntity = stockInfoMapper.getByStockId(stockId);
                    if (null != dbSHStockInfoEntity && null != dbSHStockInfoEntity.getId()) {
                        shStockInfoEntity.setId(dbSHStockInfoEntity.getId());
                        stockInfoMapper.update(shStockInfoEntity);
                    } else {
                        stockInfoMapper.insert(shStockInfoEntity);
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(stockId);
                }
            }
        }
    }
}
