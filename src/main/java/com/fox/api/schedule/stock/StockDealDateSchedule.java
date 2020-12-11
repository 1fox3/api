package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDateEntity;
import com.fox.api.dao.stock.mapper.StockDealDateMapper;
import com.fox.api.service.stock.StockDealDateService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.constant.StockConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 股市交易日期同步任务
 *
 * @author lusongsong
 * @date 2020/11/26 13:53
 */
@Component
public class StockDealDateSchedule extends StockBaseSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 至少同步的日历
     */
    private static final int DATE_MIN_SYNC = 14;
    /**
     * 至少向后同步出的交易日期天数
     */
    private static final int DEAL_DATE_NEXT_MIN_SYNC = 5;
    /**
     * 至少向前同步出的交易日期天数
     */
    private static final int DEAL_DATE_PRE_MIN_SYNC = 1;

    /**
     * 交易日期数据库操作类
     */
    @Autowired
    StockDealDateMapper stockDealDateMapper;

    /**
     * 同步股市交易日期
     */
    @LogShowTimeAnt
    public void syncSMDealDate() {
        String date;
        int type;
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            int i = -1;
            int dealDateNum = 0;
            //向前更新交易日期
            while (true) {
                if (dealDateNum >= DEAL_DATE_PRE_MIN_SYNC) {
                    break;
                }
                date = DateUtil.getRelateDate(0, 0, i, DateUtil.DATE_FORMAT_1);
                type = syncSingleDate(stockMarket, date);
                if (StockDealDateService.DEAL_DATE_YES == type) {
                    dealDateNum++;
                }
                i--;
            }
            i = 0;
            dealDateNum = 0;
            //向后更新交易日期
            while (true) {
                if (i >= DATE_MIN_SYNC && dealDateNum >= DEAL_DATE_NEXT_MIN_SYNC) {
                    break;
                }
                date = DateUtil.getRelateDate(0, 0, i, DateUtil.DATE_FORMAT_1);
                type = syncSingleDate(stockMarket, date);
                if (StockDealDateService.DEAL_DATE_YES == type) {
                    dealDateNum++;
                }
                i++;
            }
        }
    }

    /**
     * 同步单天
     *
     * @param stockMarket
     * @param date
     * @return
     */
    private int syncSingleDate(Integer stockMarket, String date) {
        try {
            int type = isDealDate(stockMarket, date) ?
                    StockDealDateService.DEAL_DATE_YES : StockDealDateService.DEAL_DATE_NO;
            StockDealDateEntity stockDealDateEntity = new StockDealDateEntity();
            stockDealDateEntity.setStockMarket(stockMarket);
            stockDealDateEntity.setDt(date);
            StockDealDateEntity dbStockDealDateEntity = stockDealDateMapper.get(stockDealDateEntity);
            if (null != dbStockDealDateEntity) {
                Boolean needUpdate = true;
                if (StockDealDateService.DATE_LOCKED == dbStockDealDateEntity.getIsLocked()) {
                    if (StockDealDateService.DEAL_DATE_YES == dbStockDealDateEntity.getType()
                            || StockDealDateService.DEAL_DATE_NO == dbStockDealDateEntity.getType()) {
                        type = dbStockDealDateEntity.getType();
                        needUpdate = false;
                    }
                }
                if (needUpdate) {
                    dbStockDealDateEntity.setStockMarket(stockMarket);
                    dbStockDealDateEntity.setDt(date);
                    dbStockDealDateEntity.setType(type);
                    dbStockDealDateEntity.setIsLocked(StockDealDateService.DATE_UNLOCKED);
                    stockDealDateMapper.update(dbStockDealDateEntity);
                }
            } else {
                stockDealDateEntity.setType(type);
                stockDealDateEntity.setIsLocked(StockDealDateService.DATE_UNLOCKED);
                stockDealDateMapper.insert(stockDealDateEntity);
            }
            return type;
        } catch (Exception e) {
            logger.error(stockMarket.toString());
            logger.error(date);
            logger.error(e.getMessage());
        }
        return StockDealDateService.DEAL_DATE_NO;
    }

    /**
     * 更新股市近的3个交易日期
     */
    @LogShowTimeAnt
    public void syncStockMarketAroundDealDate() {
        String currentDate = DateUtil.getCurrentDate();
        StockDealDateEntity stockDealDateEntity = new StockDealDateEntity();
        stockDealDateEntity.setDt(currentDate);
        for (Integer stockMarket : StockConst.SM_ALL) {
            stockDealDateEntity.setStockMarket(stockMarket);
            if (StockConst.SM_A_LIST.contains(stockMarket)) {
                stockDealDateEntity.setStockMarket(StockConst.SM_A);
            }

            //更新上个交易日
            StockDealDateEntity preStockDealDateEntity = stockDealDateMapper.pre(stockDealDateEntity);
            if (null != preStockDealDateEntity) {
                stockRedisUtil.set(StockUtil.preDealDateCacheKey(stockMarket), preStockDealDateEntity.getDt());
            }
            //更新当前交易日
            StockDealDateEntity lastStockDealDateEntity = stockDealDateMapper.last(stockDealDateEntity);
            if (null != lastStockDealDateEntity) {
                stockRedisUtil.set(StockUtil.lastDealDateCacheKey(stockMarket), lastStockDealDateEntity.getDt());
            }
            //更新下一个交易日
            StockDealDateEntity nextStockDealDateEntity = stockDealDateMapper.next(stockDealDateEntity);
            if (null != nextStockDealDateEntity) {
                stockRedisUtil.set(StockUtil.nextDealDateCacheKey(stockMarket), nextStockDealDateEntity.getDt());
            }
        }
    }
}
