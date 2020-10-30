package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.stock.StockConst;
import com.fox.api.constant.stock.StockTableDtConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockPriceDealNumDayEntity;
import com.fox.api.dao.stock.entity.StockTableDtEntity;
import com.fox.api.dao.stock.mapper.StockPriceDealNumDayMapper;
import com.fox.api.entity.po.third.stock.StockDealNumPo;
import com.fox.api.service.stock.StockTableDtService;
import com.fox.api.service.third.stock.sina.api.SinaDealRatio;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理股票交易按天价格成交量数据
 *
 * @author lusongsong
 * @date 2020/10/30 15:50
 */
public class StockPriceDealNumDaySchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 每次备份记录条数
     */
    private Integer bakOneLimit = 100000;
    /**
     * 需要保存的数据天数
     */
    private Integer totalDateNum = 50;
    /**
     * 需要处理的交易日
     */
    private String dealDate;

    @Autowired
    SinaDealRatio sinaDealRatio;
    @Autowired
    StockPriceDealNumDayMapper stockPriceDealNumDayMapper;
    @Autowired
    StockTableDtService stockTableDtService;

    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == dealDate || null == stockEntity
                || null == stockEntity.getStockCode() || null == stockEntity.getStockMarket()) {
            return;
        }

        List<StockDealNumPo> stockDealNumPoList = sinaDealRatio.getDealRatio(stockEntity, dealDate, dealDate);
        if (null == stockDealNumPoList || stockDealNumPoList.isEmpty()) {
            return;
        }
        List<StockPriceDealNumDayEntity> stockPriceDealNumDayEntityList = new ArrayList<>(stockDealNumPoList.size());
        for (StockDealNumPo stockDealNumPo : stockDealNumPoList) {
            if (null == stockDealNumPo || null == stockDealNumPo.getPrice() || null == stockDealNumPo.getDealNum()) {
                continue;
            }
            StockPriceDealNumDayEntity stockPriceDealNumDayEntity =  new StockPriceDealNumDayEntity();
            stockPriceDealNumDayEntity.setStockId(stockEntity.getId());
            stockPriceDealNumDayEntity.setDt(dealDate);
            stockPriceDealNumDayEntity.setPrice(stockDealNumPo.getPrice());
            stockPriceDealNumDayEntity.setDealNum(stockDealNumPo.getDealNum());
            stockPriceDealNumDayEntityList.add(stockPriceDealNumDayEntity);
        }
        if (stockPriceDealNumDayEntityList.isEmpty()) {
            return;
        }
        stockPriceDealNumDayMapper.deleteByDate(stockEntity.getId(), dealDate);
        stockPriceDealNumDayMapper.batchInsert(stockPriceDealNumDayEntityList);
    }

    /**
     * 记录日期
     */
    private void logDt() {
        StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
        stockTableDtEntity.setTable(StockTableDtConst.TABLE_PRICE_DEAL_NUM_DAY);
        stockTableDtEntity.setDt(dealDate);
        stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
        stockTableDtService.insert(stockTableDtEntity);
    }

    /**
     * 备份数据
     */
    private void bakPriceDealNumDayInfo() {
        stockPriceDealNumDayMapper.createBak();
        StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
        stockTableDtEntity.setTable(StockTableDtConst.TABLE_PRICE_DEAL_NUM_DAY);
        stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
        stockTableDtService.insert(stockTableDtEntity);
        List<String> dtList = stockTableDtService.getByType(stockTableDtEntity);
        if (null == dtList || dtList.isEmpty() || dtList.size() < totalDateNum) {
            return;
        }
        Integer bakDtNum = dtList.size() - totalDateNum;
        for (int i = 0; i < bakDtNum; i++) {
            while (true) {
                stockPriceDealNumDayMapper.bakByDate(dtList.get(i), bakOneLimit);
                Integer delNum = stockPriceDealNumDayMapper.clearByDate(dtList.get(i), bakOneLimit);
                if (null == delNum || delNum < bakOneLimit) {
                    break;
                }
            }
        }
    }

    /**
     * 同步交易日当天的价格成交量数据
     */
    @LogShowTimeAnt
    public void syncLastDealDatePriceDealNumDayInfo() {
        if (StockUtil.todayIsDealDate(StockConst.SM_A)) {
            dealDate = DateUtil.getCurrentDate();
            logDt();
            aStockMarketScan(this);
            bakPriceDealNumDayInfo();
            stockPriceDealNumDayMapper.optimize();
        }
    }

    /**
     * 同步全部的价格成交量数据
     */
    @LogShowTimeAnt
    public void syncTotalPriceDealNumDayInfo() {
        List<String> dateList = StockUtil.getDealDateList(StockConst.SM_A, totalDateNum);
        if (null == dateList || dateList.isEmpty()) {
            return;
        }
        for (String dt : dateList) {
            dealDate = DateUtil.getCurrentDate();
            logDt();
            aStockMarketScan(this);
        }
        stockPriceDealNumDayMapper.optimize();
    }
}
