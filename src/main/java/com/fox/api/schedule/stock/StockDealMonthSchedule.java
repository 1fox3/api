package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockDealMonthEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockDealMonthMapper;
import com.fox.api.schedule.stock.handler.StockScheduleHandler;
import com.fox.api.util.DateUtil;
import com.fox.spider.stock.constant.StockConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 股票月粒度交易数据同步
 *
 * @author lusongsong
 * @date 2020/10/20 21:24
 */
@Component
public class StockDealMonthSchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 单次批量插入限制
     */
    private static int BATCH_INSERT_LIMIT = 200;
    /**
     * 按月交易数据库操作类
     */
    @Autowired
    StockDealMonthMapper stockDealMonthMapper;
    /**
     * 按天交易数据库操作类
     */
    @Autowired
    StockDealDayMapper stockDealDayMapper;
    /**
     * 复权类型
     */
    List<Integer> fqTypeList = Arrays.asList(
            StockConst.SFQ_BEFORE,
            StockConst.SFQ_AFTER
    );
    /**
     * 是否同步当周数据
     */
    private Boolean syncTotal = false;
    /**
     * 日期类型
     */
    private String dateType = "month";
    /**
     * 日期范围列表
     */
    private List<String> monthList;
    /**
     * 当前股票按天交易信息
     */
    private List<StockDealDayEntity> totalStockDealDayEntityList = null;

    /**
     * 创建影子表
     */
    private void createShadow() {
        stockDealMonthMapper.createShadow();
    }

    /**
     * 数据表重命名
     */
    private void shadowConvert() {
        stockDealMonthMapper.shadowConvert();
    }

    /**
     * 删除影子表
     */
    private void dropShadow() {
        stockDealMonthMapper.dropShadow();
    }

    /**
     * 数据表重命名
     */
    private void optimizeTable() {
        stockDealMonthMapper.optimize();
    }

    /**
     * 设置月份范围
     *
     * @param startDate
     * @param endDate
     */
    private void setMonthList(String startDate, String endDate) {
        Map<String, String> dateScopeMap = new HashMap<>(2);
        dateScopeMap.put("startDate", startDate);
        dateScopeMap.put("endDate", endDate);
        try {
            monthList = doDateByStatisticsType(dateType, dateScopeMap);
        } catch (ParseException e) {
            logger.error("setMonthList", e);
        }

    }

    /**
     * 按月汇总数据
     *
     * @param stockDealDayEntityList
     * @return
     */
    private StockDealMonthEntity handleMonthDealInfo(List<StockDealDayEntity> stockDealDayEntityList) {
        if (null == stockDealDayEntityList | stockDealDayEntityList.isEmpty()) {
            return null;
        }
        StockDealMonthEntity stockDealMonthEntity = new StockDealMonthEntity();
        BigDecimal highestPrice = null, lowestPrice = null, dealMoney = null;
        Long dealNum = null;
        for (int i = 0; i < stockDealDayEntityList.size(); i++) {
            StockDealDayEntity stockDealDayEntity = stockDealDayEntityList.get(i);
            if (0 == i) {
                stockDealMonthEntity.setStockId(stockDealDayEntity.getStockId());
                stockDealMonthEntity.setFqType(stockDealDayEntity.getFqType());
                stockDealMonthEntity.setOpenPrice(stockDealDayEntity.getOpenPrice());
                stockDealMonthEntity.setPreClosePrice(stockDealDayEntity.getPreClosePrice());
            }

            if (i == stockDealDayEntityList.size() - 1) {
                stockDealMonthEntity.setDt(stockDealDayEntity.getDt());
                stockDealMonthEntity.setClosePrice(stockDealDayEntity.getClosePrice());
                stockDealMonthEntity.setCircEquity(stockDealDayEntity.getCircEquity());
                stockDealMonthEntity.setTotalEquity(stockDealDayEntity.getTotalEquity());
            }
            if (null == highestPrice) {
                highestPrice = stockDealDayEntity.getHighestPrice();
            } else {
                if (null != stockDealDayEntity.getHighestPrice()) {
                    highestPrice = 0 <= highestPrice.compareTo(stockDealDayEntity.getHighestPrice()) ?
                            highestPrice : stockDealDayEntity.getHighestPrice();
                }
            }
            if (null == lowestPrice) {
                lowestPrice = stockDealDayEntity.getLowestPrice();
            } else {
                if (null != stockDealDayEntity.getLowestPrice()) {
                    lowestPrice = 0 >= lowestPrice.compareTo(stockDealDayEntity.getLowestPrice()) ?
                            lowestPrice : stockDealDayEntity.getLowestPrice();
                }
            }
            if (null == dealMoney) {
                dealMoney = stockDealDayEntity.getDealMoney();
            } else {
                if (null != stockDealDayEntity.getDealMoney()) {
                    dealMoney = dealMoney.add(stockDealDayEntity.getDealMoney());
                }
            }
            if (null == dealNum) {
                dealNum = stockDealDayEntity.getDealNum();
            } else {
                if (null != stockDealDayEntity.getDealNum()) {
                    dealNum += stockDealDayEntity.getDealNum();
                }
            }
        }
        stockDealMonthEntity.setHighestPrice(highestPrice);
        stockDealMonthEntity.setLowestPrice(lowestPrice);
        stockDealMonthEntity.setDealMoney(dealMoney);
        stockDealMonthEntity.setDealNum(dealNum);
        return stockDealMonthEntity;
    }

    /**
     * 根据日期获取股票按天交易信息
     *
     * @param fqType
     * @param stockId
     * @return
     */
    private List<StockDealDayEntity> getStockDealDayEntityList(
            Integer stockId, Integer fqType, String startDate, String endDate
    ) {
        if (syncTotal) {
            if (null == totalStockDealDayEntityList) {
                totalStockDealDayEntityList = stockDealDayMapper.getTotalByStock(stockId, fqType);
            }
            if (null != totalStockDealDayEntityList) {
                List<StockDealDayEntity> stockDealDayEntityList = new ArrayList<>();
                for (int i = 0; i < totalStockDealDayEntityList.size(); i++) {
                    StockDealDayEntity stockDealDayEntity = totalStockDealDayEntityList.get(i);
                    try {
                        if (DateUtil.compare(stockDealDayEntity.getDt(), startDate, DateUtil.DATE_FORMAT_1) >= 0 &&
                                DateUtil.compare(stockDealDayEntity.getDt(), endDate, DateUtil.DATE_FORMAT_1) <= 0
                        ) {
                            stockDealDayEntityList.add(stockDealDayEntity);
                            totalStockDealDayEntityList.remove(i);
                        }
                    } catch (ParseException e) {
                        logger.error("getStockDealDayEntityList", e);
                    }
                }
                return stockDealDayEntityList;
            }
            return null;
        } else {
            return stockDealDayMapper.getByDate(stockId, fqType, startDate, endDate);
        }
    }

    /**
     * 同步按月数据
     *
     * @param stockEntity
     */
    private void syncMonthDealInfo(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode() || stockEntity.getStockCode().isEmpty()
                || null == monthList || monthList.isEmpty()) {
            return;
        }
        int monthLen = monthList.size();
        List<StockDealMonthEntity> stockDealMonthEntityList = new ArrayList<>();
        for (int fqType : fqTypeList) {
            totalStockDealDayEntityList = null;
            for (int i = 0; i < monthLen; i += 2) {
                String monthStartDate = monthList.get(i);
                String monthEndDate = monthList.get(i + 1);
                if (null == monthStartDate || null == monthEndDate || monthStartDate.isEmpty() || monthEndDate.isEmpty()) {
                    continue;
                }
                List<StockDealDayEntity> stockDealDayEntityList = getStockDealDayEntityList(
                        stockEntity.getId(), fqType, monthStartDate, monthEndDate
                );
                try {
                    StockDealMonthEntity stockDealMonthEntity = handleMonthDealInfo(stockDealDayEntityList);
                    if (null != stockDealMonthEntity) {
                        stockDealMonthEntityList.add(stockDealMonthEntity);
                    }
                } catch (Exception e) {
                    logger.error(stockEntity.toString());
                    logger.error("handleMonthDealInfo", e);
                }
            }
        }
        if (null != stockDealMonthEntityList && !stockDealMonthEntityList.isEmpty()) {
            if (syncTotal) {
                try {
                    for (int i = 0; i < stockDealMonthEntityList.size() - 1; i += BATCH_INSERT_LIMIT) {
                        int topIndex = i + BATCH_INSERT_LIMIT - 1;
                        topIndex = topIndex > stockDealMonthEntityList.size() - 1 ?
                                stockDealMonthEntityList.size() - 1 : topIndex;
                        stockDealMonthMapper.batchInsert(stockDealMonthEntityList.subList(i, topIndex));
                    }
                } catch (Exception e) {
                    logger.error("batchInsert", e);
                    logger.error("rows", stockDealMonthEntityList);
                }
            } else {
                for (StockDealMonthEntity stockDealMonthEntity : stockDealMonthEntityList) {
                    try {
                        StockDealMonthEntity dbStockDealMonthEntity = stockDealMonthMapper.getBySignalDate(stockDealMonthEntity);
                        if (null != dbStockDealMonthEntity) {
                            stockDealMonthEntity.setId(dbStockDealMonthEntity.getId());
                            stockDealMonthMapper.update(stockDealMonthEntity);
                        } else {
                            stockDealMonthMapper.insert(stockDealMonthEntity);
                        }
                    } catch (Exception e) {
                        logger.error("insert", e);
                        logger.error("row", stockDealMonthEntity);
                    }
                }
            }
        }
    }


    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode()
                || stockEntity.getStockCode().isEmpty()) {
            return;
        }
        syncMonthDealInfo(stockEntity);
    }

    /**
     * 同步所有的按周交易信息数据
     */
    @LogShowTimeAnt
    public void syncTotalDealMonthInfo() {
        try {
            syncTotal = true;
            dropShadow();
            createShadow();
            for (Integer sm : StockConst.SM_A_LIST) {
                //交易开始日期
                String startDate = StockConst.stockMarketStartDate(sm);
                if (null == startDate) {
                    continue;
                }
                String endDate = DateUtil.getCurrentDate();
                setMonthList(startDate, endDate);
                stockMarketScan(sm, this);
            }
            shadowConvert();
            dropShadow();
            optimizeTable();
        } catch (Exception e) {
            logger.error("syncTotalDealDayInfo", e);
        }
    }

    /**
     * 同步交易当周信息数据
     */
    @LogShowTimeAnt
    public void syncCurrentDealMonthInfo() {
        try {
            syncTotal = false;
            String startDate = DateUtil.getCurrentDate();
            String endDate = startDate.substring(0, 8) + "01";
            setMonthList(startDate, endDate);
            for (Integer sm : StockConst.SM_A_LIST) {
                stockMarketScan(sm, this);
            }
        } catch (Exception e) {
            logger.error("syncCurrentDealMonthInfo", e);
        }
    }
}
