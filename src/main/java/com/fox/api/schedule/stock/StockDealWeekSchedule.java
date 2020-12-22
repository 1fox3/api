package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockDealWeekEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockDealWeekMapper;
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
 * 股票周粒度交易数据同步
 *
 * @author lusongsong
 * @date 2020/10/20 16:31
 */
@Component
public class StockDealWeekSchedule extends StockBaseSchedule implements StockScheduleHandler {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 单次批量插入限制
     */
    private static int BATCH_INSERT_LIMIT = 200;
    /**
     * 按周交易数据库操作类
     */
    @Autowired
    StockDealWeekMapper stockDealWeekMapper;
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
    private String dateType = "week";
    /**
     * 日期范围列表
     */
    private List<String> weekList;
    /**
     * 当前股票按天交易信息
     */
    private List<StockDealDayEntity> totalStockDealDayEntityList = null;

    /**
     * 创建影子表
     */
    private void createShadow() {
        stockDealWeekMapper.createShadow();
    }

    /**
     * 数据表重命名
     */
    private void shadowConvert() {
        stockDealWeekMapper.shadowConvert();
    }

    /**
     * 删除影子表
     */
    private void dropShadow() {
        stockDealWeekMapper.dropShadow();
    }

    /**
     * 数据表重命名
     */
    private void optimizeTable() {
        stockDealWeekMapper.optimize();
    }

    /**
     * 设置周份范围
     *
     * @param startDate
     * @param endDate
     */
    private void setWeekList(String startDate, String endDate) {
        Map<String, String> dateScopeMap = new HashMap<>(2);
        dateScopeMap.put("startDate", startDate);
        dateScopeMap.put("endDate", endDate);
        try {
            weekList = doDateByStatisticsType(dateType, dateScopeMap);
        } catch (ParseException e) {
            logger.error("setWeekList", e);
        }
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
     * 按周汇总数据
     *
     * @param stockDealDayEntityList
     * @return
     */
    private StockDealWeekEntity handleWeekDealInfo(List<StockDealDayEntity> stockDealDayEntityList) {
        if (null == stockDealDayEntityList | stockDealDayEntityList.isEmpty()) {
            return null;
        }
        StockDealWeekEntity stockDealWeekEntity = new StockDealWeekEntity();
        BigDecimal highestPrice = null, lowestPrice = null, dealMoney = null;
        Long dealNum = null;
        for (int i = 0; i < stockDealDayEntityList.size(); i++) {
            StockDealDayEntity stockDealDayEntity = stockDealDayEntityList.get(i);
            if (0 == i) {
                stockDealWeekEntity.setStockId(stockDealDayEntity.getStockId());
                stockDealWeekEntity.setFqType(stockDealDayEntity.getFqType());
                stockDealWeekEntity.setOpenPrice(stockDealDayEntity.getOpenPrice());
                stockDealWeekEntity.setPreClosePrice(stockDealDayEntity.getPreClosePrice());
            }

            if (i == stockDealDayEntityList.size() - 1) {
                stockDealWeekEntity.setDt(stockDealDayEntity.getDt());
                stockDealWeekEntity.setClosePrice(stockDealDayEntity.getClosePrice());
                stockDealWeekEntity.setCircEquity(stockDealDayEntity.getCircEquity());
                stockDealWeekEntity.setTotalEquity(stockDealDayEntity.getTotalEquity());
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
        stockDealWeekEntity.setHighestPrice(highestPrice);
        stockDealWeekEntity.setLowestPrice(lowestPrice);
        stockDealWeekEntity.setDealMoney(dealMoney);
        stockDealWeekEntity.setDealNum(dealNum);
        return stockDealWeekEntity;
    }

    /**
     * 同步按周数据
     *
     * @param stockEntity
     */
    private void syncWeekDealInfo(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getStockCode() || stockEntity.getStockCode().isEmpty()
                || null == weekList || weekList.isEmpty()) {
            return;
        }
        int weekLen = weekList.size();
        List<StockDealWeekEntity> stockDealWeekEntityList = new ArrayList<>();
        for (int fqType : fqTypeList) {
            totalStockDealDayEntityList = null;
            for (int i = 0; i < weekLen; i += 2) {
                String weekStartDate = weekList.get(i);
                String weekEndDate = weekList.get(i + 1);
                if (null == weekStartDate || null == weekEndDate || weekStartDate.isEmpty() || weekEndDate.isEmpty()) {
                    continue;
                }
                List<StockDealDayEntity> stockDealDayEntityList = getStockDealDayEntityList(
                        stockEntity.getId(), fqType, weekStartDate, weekEndDate
                );
                try {
                    StockDealWeekEntity stockDealWeekEntity = handleWeekDealInfo(stockDealDayEntityList);
                    if (null != stockDealWeekEntity) {
                        stockDealWeekEntityList.add(stockDealWeekEntity);
                    }
                } catch (Exception e) {
                    logger.error(stockEntity.toString());
                    logger.error("handleWeekDealInfo", e);
                }
            }
        }
        if (null != stockDealWeekEntityList && !stockDealWeekEntityList.isEmpty()) {
            if (syncTotal) {
                try {
                    for (int i = 0; i < stockDealWeekEntityList.size() - 1; i += BATCH_INSERT_LIMIT) {
                        int topIndex = i + BATCH_INSERT_LIMIT - 1;
                        topIndex = topIndex > stockDealWeekEntityList.size() - 1 ?
                                stockDealWeekEntityList.size() - 1 : topIndex;
                        stockDealWeekMapper.batchInsert(stockDealWeekEntityList.subList(i, topIndex));
                    }
                } catch (Exception e) {
                    logger.error("batchInsert", e);
                    logger.error("rows", stockDealWeekEntityList);
                }
            } else {
                for (StockDealWeekEntity stockDealWeekEntity : stockDealWeekEntityList) {
                    try {
                        StockDealWeekEntity dbStockDealWeekEntity = stockDealWeekMapper.getBySignalDate(stockDealWeekEntity);
                        if (null != dbStockDealWeekEntity) {
                            stockDealWeekEntity.setId(dbStockDealWeekEntity.getId());
                            stockDealWeekMapper.update(stockDealWeekEntity);
                        } else {
                            stockDealWeekMapper.insert(stockDealWeekEntity);
                        }
                    } catch (Exception e) {
                        logger.error("insert", e);
                        logger.error("row", stockDealWeekEntity);
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
        syncWeekDealInfo(stockEntity);
    }

    /**
     * 同步所有的按周交易信息数据
     */
    @LogShowTimeAnt
    public void syncTotalDealWeekInfo() {
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
                setWeekList(startDate, endDate);
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
    public void syncCurrentDealWeekInfo() {
        try {
            syncTotal = false;
            String endDate = DateUtil.getCurrentDate();
            Integer weekNum = DateUtil.getDayInWeekNum(endDate, DateUtil.DATE_FORMAT_1);
            String startDate = DateUtil.getRelateDate(
                    endDate, 0, 0, 0 == weekNum ? -6 : 1 - weekNum, DateUtil.DATE_FORMAT_1
            );
            setWeekList(startDate, endDate);
            for (Integer sm : StockConst.SM_A_LIST) {
                stockMarketScan(sm, this);
            }
        } catch (Exception e) {
            logger.error("syncCurrentDealWeekInfo", e);
        }
    }
}
