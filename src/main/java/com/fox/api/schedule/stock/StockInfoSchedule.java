package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockInfoEntity;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.service.stock.StockInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 股票信息同步任务
 * @author lusongsong
 * @date 2020/4/10 16:26
 */
@Component
public class StockInfoSchedule extends StockBaseSchedule {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private StockInfoService stockInfoService;

    /**
     * 同步所有股票的信息
     */
    @LogShowTimeAnt
    public void stockInfo() {
        Integer stockId = 0;
        Integer onceLimit = 100;
        while (true) {
            List<StockEntity> stockEntityList = this.stockMapper.getTotalByType(
                    2,
                    stockId,
                    onceLimit.toString()
            );
            if (null == stockEntityList) {
                break;
            }
            for (StockEntity stockEntity : stockEntityList) {
                if (null == stockEntity) {
                    continue;
                }
                stockId = null == stockEntity.getId() ? stockEntity.getId() : stockId + 1;
                try{
                    StockInfoEntity shStockInfoEntity = stockInfoService.getInfoFromStockExchange(stockId);
                    if (null == shStockInfoEntity.getStockOnDate() || "".equals(shStockInfoEntity.getStockOnDate())) {
                        continue;
                    }
                    StockInfoEntity dbSHStockInfoEntity = stockInfoMapper.getByStockId(stockId);
                    if (null != dbSHStockInfoEntity && null != dbSHStockInfoEntity.getId()) {
                        shStockInfoEntity.setId(dbSHStockInfoEntity.getId());
                        stockInfoMapper.update(shStockInfoEntity);
                    } else {
                        stockInfoMapper.insert(shStockInfoEntity);
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    logger.error(stockId.toString());
                    logger.error(e.getMessage());
                }
            }
            if (stockEntityList.size() < onceLimit) {
                break;
            }
        }
    }
}
