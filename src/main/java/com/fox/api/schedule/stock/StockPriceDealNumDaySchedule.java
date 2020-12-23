package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.stock.StockTableDtConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockPriceDealNumDayEntity;
import com.fox.api.dao.stock.entity.StockTableDtEntity;
import com.fox.api.dao.stock.mapper.StockPriceDealNumDayMapper;
import com.fox.api.service.stock.StockTableDtService;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.sina.SinaRealtimePriceDealNumRatioApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.sina.SinaPriceDealNumRatioPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理股票交易按天价格成交量数据
 *
 * @author lusongsong
 * @date 2020/10/30 15:50
 */
@Component
public class StockPriceDealNumDaySchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 当前交易日
     */
    private String lastDealDate;
    /**
     * 新浪股票交易日当天价格成交量占比接口
     */
    @Autowired
    SinaRealtimePriceDealNumRatioApi sinaRealtimePriceDealNumRatioApi;
    /**
     * 股票价格成交量数据库操作类
     */
    @Autowired
    StockPriceDealNumDayMapper stockPriceDealNumDayMapper;
    /**
     * 数据库数据日期维护服务
     */
    @Autowired
    StockTableDtService stockTableDtService;

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        try {
            if (null == lastDealDate || null == stockEntity
                    || null == stockEntity.getStockCode() || null == stockEntity.getStockMarket()) {
                return;
            }

            List<SinaPriceDealNumRatioPo> sinaPriceDealNumRatioPoList =
                    sinaRealtimePriceDealNumRatioApi.priceDealNumRatio(
                            new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket())
                    );

            if (null == sinaPriceDealNumRatioPoList || sinaPriceDealNumRatioPoList.isEmpty()) {
                return;
            }

            List<StockPriceDealNumDayEntity> stockPriceDealNumDayEntityList =
                    new ArrayList<>(sinaPriceDealNumRatioPoList.size());
            for (SinaPriceDealNumRatioPo sinaPriceDealNumRatioPo : sinaPriceDealNumRatioPoList) {
                if (null == sinaPriceDealNumRatioPo || null == sinaPriceDealNumRatioPo.getPrice()
                        || null == sinaPriceDealNumRatioPo.getDealNum()) {
                    continue;
                }
                StockPriceDealNumDayEntity stockPriceDealNumDayEntity = new StockPriceDealNumDayEntity();
                stockPriceDealNumDayEntity.setStockId(stockEntity.getId());
                stockPriceDealNumDayEntity.setDt(lastDealDate);
                stockPriceDealNumDayEntity.setPrice(sinaPriceDealNumRatioPo.getPrice());
                stockPriceDealNumDayEntity.setDealNum(sinaPriceDealNumRatioPo.getDealNum());
                stockPriceDealNumDayEntityList.add(stockPriceDealNumDayEntity);
            }
            if (stockPriceDealNumDayEntityList.isEmpty()) {
                return;
            }
            stockPriceDealNumDayMapper.deleteByDate(stockEntity.getId(), lastDealDate);
            stockPriceDealNumDayMapper.batchInsert(stockPriceDealNumDayEntityList);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 记录日期
     */
    private void logDt() {
        StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
        stockTableDtEntity.setTable(StockTableDtConst.TABLE_PRICE_DEAL_NUM_DAY);
        stockTableDtEntity.setDt(lastDealDate);
        stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
        stockTableDtService.insert(stockTableDtEntity);
    }

    /**
     * 同步数据
     *
     * @param stockMarket
     */
    private void syncStockMarketData(Integer stockMarket) {
        if (null == stockMarket || !StockConst.SM_ALL.contains(stockMarket)) {
            return;
        }
        //同步股票
        stockMarketScan(stockMarket, this);
    }

    /**
     * 同步交易日当天的价格成交量数据
     */
    @LogShowTimeAnt
    public void syncLastDealDatePriceDealNumDayInfo(Integer stockMarket) {
        if (StockConst.SM_ALL.contains(stockMarket) && StockUtil.todayIsDealDate(stockMarket)) {
            lastDealDate = StockUtil.lastDealDate(stockMarket);
            logDt();
            if (StockConst.SM_A == stockMarket) {
                for (Integer sm : StockConst.SM_A_LIST) {
                    syncStockMarketData(sm);
                }
            } else {
                syncStockMarketData(stockMarket);
            }
            stockPriceDealNumDayMapper.optimize();
        }
    }
}
