package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockInfoEntity;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.schedule.stock.handler.StockScheduleHandler;
import com.fox.api.service.stock.StockInfoService;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 股票信息同步任务
 *
 * @author lusongsong
 * @date 2020/4/10 16:26
 */
@Component
public class StockInfoSchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StockInfoMapper stockInfoMapper;
    @Autowired
    private StockInfoService stockInfoService;

    /**
     * 同步所有股票的信息
     */
    @LogShowTimeAnt
    public void syncStockInfo() {
        allStockMarketScan(this);
    }

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity) {
            return;
        }
        try {
            StockInfoEntity shStockInfoEntity = stockInfoService.getInfoFromStockExchange(
                    new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket())
            );
            if (null == shStockInfoEntity || null == shStockInfoEntity.getStockOnDate()
                    || "".equals(shStockInfoEntity.getStockOnDate())) {
                return;
            }
            StockInfoEntity dbSHStockInfoEntity = stockInfoMapper.getByStockId(stockEntity.getId());
            if (null != dbSHStockInfoEntity && null != dbSHStockInfoEntity.getId()) {
                shStockInfoEntity.setId(dbSHStockInfoEntity.getId());
                stockInfoMapper.update(shStockInfoEntity);
            } else {
                stockInfoMapper.insert(shStockInfoEntity);
            }
            Thread.sleep(2000);
        } catch (Exception e) {
            logger.error(stockEntity.getId().toString());
            logger.error(e.getMessage());
        }
    }
}
