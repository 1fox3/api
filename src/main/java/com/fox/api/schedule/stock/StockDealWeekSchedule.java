package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.*;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockDealWeekMapper;
import com.fox.api.dao.stock.mapper.StockPriceDayMapper;
import com.fox.api.dao.stock.mapper.StockPriceWeekMapper;
import com.fox.api.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 股票周粒度交易数据同步
 * @author lusongsong
 * @date 2020/10/20 16:31
 */
@Component
public class StockDealWeekSchedule extends StockBaseSchedule implements StockScheduleHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    StockPriceWeekMapper stockPriceWeekMapper;
    @Autowired
    StockDealWeekMapper stockDealWeekMapper;
    @Autowired
    StockPriceDayMapper stockPriceDayMapper;
    @Autowired
    StockDealDayMapper stockDealDayMapper;

    /**
     * 复权类型
     */
    List<Integer> fqTypeList = Arrays.asList(0, 1);
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
     * 当前周开始时间
     */
    private String currentWeekStartDate;
    /**
     * 当前周结束时间
     */
    private String currentWeekEndDate;
    /**
     * 创建影子表
     */
    private void createShadow() {
        stockPriceWeekMapper.createShadow();
        stockDealWeekMapper.createShadow();
    }

    /**
     * 数据表重命名
     */
    private void shadowConvert() {
        stockPriceWeekMapper.shadowConvert();
        stockDealWeekMapper.shadowConvert();
    }

    /**
     * 删除影子表
     */
    private void dropShadow() {
        stockPriceWeekMapper.dropShadow();
        stockDealWeekMapper.dropShadow();
    }

    /**
     * 数据表重命名
     */
    private void optimizeTable() {
        stockPriceWeekMapper.optimize();
        stockDealWeekMapper.optimize();
    }

    /**
     * 查询价格列表
     * @param fqType
     * @param stockId
     * @return
     */
    private List<StockPriceDayEntity> getStockPriceDayEntityList(Integer fqType, Integer stockId) {
        return syncTotal ? stockPriceDayMapper.getTotalByStock(fqType, stockId)
                : stockPriceDayMapper.getByDate(fqType, stockId, currentWeekStartDate, currentWeekEndDate);
    }

    /**
     * 查询交易列表
     * @param stockId
     * @return
     */
    private List<StockDealDayEntity> getStockDealDayEntityList(Integer stockId) {
        return syncTotal ? stockDealDayMapper.getTotalByStock(stockId)
                : stockDealDayMapper.getByDate(stockId, currentWeekStartDate, currentWeekEndDate);
    }

    /**
     * 同步价格
     * @param stockEntity
     */
    private void syncPrice(StockEntity stockEntity) {
        StockPriceWeekEntity stockPriceWeekEntity = null, dbStockPriceWeekEntity;
        List<StockPriceWeekEntity> stockPriceWeekEntityList = new LinkedList<>();
        for (Integer fqType : fqTypeList) {
            List<StockPriceDayEntity> stockPriceDayEntityList = getStockPriceDayEntityList(
                    fqType, stockEntity.getId()
            );
            if (null == stockPriceDayEntityList || stockPriceDayEntityList.isEmpty()) {
                continue;
            }
            Integer weekPos = 0;
            Boolean changeWeek = false;
            String dt = null, weekStartDate = null, weekEndDate = null;
            BigDecimal openPrice = null, closePrice = null, highestPrice = null;
            BigDecimal lowestPrice = null, preClosePrice = null;
            for (StockPriceDayEntity stockPriceDayEntity : stockPriceDayEntityList) {
                if (null == stockPriceDayEntity) {
                    continue;
                }
                //忽略当日无交易的脏数据
                if (null != stockPriceDayEntity.getClosePrice()
                        && 0 == BigDecimal.ZERO.compareTo(stockPriceDayEntity.getClosePrice())) {
                    continue;
                }
                try {
                    while (null == weekStartDate || null == weekEndDate
                            || DateUtil.compare(weekEndDate, stockPriceDayEntity.getDt(), DateUtil.DATE_FORMAT_1)) {
                        changeWeek = true;
                        if (weekPos > weekList.size() - 1) {
                            break;
                        }
                        weekStartDate = weekList.get(weekPos);
                        weekEndDate = weekList.get(weekPos + 1);
                        weekPos += 2;
                    }
                    if (changeWeek) {
                        changeWeek = false;
                        stockPriceWeekEntity = new StockPriceWeekEntity();
                        stockPriceWeekEntity.setFqType(fqType);
                        stockPriceWeekEntity.setStockId(stockEntity.getId());
                        if (null != closePrice) {
                            stockPriceWeekEntity.setDt(dt);
                            stockPriceWeekEntity.setOpenPrice(openPrice);
                            stockPriceWeekEntity.setClosePrice(closePrice);
                            stockPriceWeekEntity.setHighestPrice(highestPrice);
                            stockPriceWeekEntity.setLowestPrice(lowestPrice);
                            stockPriceWeekEntity.setPreClosePrice(preClosePrice);
                            stockPriceWeekEntityList.add(stockPriceWeekEntity);
                            preClosePrice = closePrice;
                        }
                        openPrice = null;
                        closePrice = null;
                        highestPrice = null;
                        lowestPrice = null;
                    }
                    dt = stockPriceDayEntity.getDt();
                    openPrice = null == openPrice
                            && null != stockPriceDayEntity.getOpenPrice()
                            && 0 > BigDecimal.ZERO.compareTo(stockPriceDayEntity.getOpenPrice())
                            ? stockPriceDayEntity.getOpenPrice() : openPrice;
                    closePrice = null == closePrice || (
                            null != stockPriceDayEntity.getClosePrice()
                                    && 0 > BigDecimal.ZERO.compareTo(stockPriceDayEntity.getClosePrice())
                            ) ? stockPriceDayEntity.getClosePrice() : closePrice;
                    highestPrice = null == highestPrice || (
                            null != stockPriceDayEntity.getHighestPrice()
                                    && 0 > highestPrice.compareTo(stockPriceDayEntity.getHighestPrice())
                            ) ? stockPriceDayEntity.getHighestPrice() : highestPrice;
                    lowestPrice = null == lowestPrice || (
                            null != stockPriceDayEntity.getLowestPrice()
                                    && 0 > stockPriceDayEntity.getLowestPrice().compareTo(lowestPrice)
                            ) ? stockPriceDayEntity.getLowestPrice() : lowestPrice;
                    preClosePrice = null == preClosePrice ? openPrice : preClosePrice;
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            //处理最后一周的数据
            if (null != closePrice) {
                stockPriceWeekEntity = new StockPriceWeekEntity();
                stockPriceWeekEntity.setFqType(fqType);
                stockPriceWeekEntity.setStockId(stockEntity.getId());
                stockPriceWeekEntity.setDt(dt);
                stockPriceWeekEntity.setOpenPrice(openPrice);
                stockPriceWeekEntity.setClosePrice(closePrice);
                stockPriceWeekEntity.setHighestPrice(highestPrice);
                stockPriceWeekEntity.setLowestPrice(lowestPrice);
                stockPriceWeekEntity.setPreClosePrice(preClosePrice);
                stockPriceWeekEntityList.add(stockPriceWeekEntity);
            }
            if (!stockPriceWeekEntityList.isEmpty()) {
                if (syncTotal) {
                    stockPriceWeekMapper.batchInsert(stockPriceWeekEntityList);
                } else {
                    if (null != stockPriceWeekEntity) {
                        List<StockPriceWeekEntity> dbStockPriceWeekEntityList = stockPriceWeekMapper.getTotalByStock(
                                fqType, stockEntity.getId()
                        );
                        if (null != dbStockPriceWeekEntityList && !dbStockPriceWeekEntityList.isEmpty()) {
                            dbStockPriceWeekEntity = dbStockPriceWeekEntityList.get(
                                    dbStockPriceWeekEntityList.size() - 1
                            );
                            String lastDt = dbStockPriceWeekEntity.getDt();
                            try {
                                stockPriceWeekEntity.setPreClosePrice(dbStockPriceWeekEntity.getClosePrice());
                                if (DateUtil.compare(lastDt, currentWeekStartDate, DateUtil.DATE_FORMAT_1)) {
                                    stockPriceWeekMapper.insert(stockPriceWeekEntity);
                                } else {
                                    stockPriceWeekEntity.setId(dbStockPriceWeekEntity.getId());
                                    stockPriceWeekMapper.update(stockPriceWeekEntity);
                                }
                            } catch (ParseException e) {
                                logger.error(e.getMessage());
                            }
                        } else {
                            stockPriceWeekMapper.insert(stockPriceWeekEntity);
                        }
                    }
                }
            }
            stockPriceDayEntityList.clear();
            stockPriceWeekEntityList.clear();
        }
    }



    /**
     * 同步交易
     * @param stockEntity
     */
    private void syncDeal(StockEntity stockEntity) {
        StockDealWeekEntity stockDealWeekEntity = null, dbStockDealWeekEntity;
        List<StockDealWeekEntity> stockDealWeekEntityList = new LinkedList<>();
        Integer weekPos = 0;
        Boolean changeWeek = false;
        String dt = null, weekStartDate = null, weekEndDate = null;
        BigDecimal closePrice = null, dealMoney = null;
        Long dealNum = null, circEquity = null, totalEquity = null;
        List<StockDealDayEntity> stockDealDayEntityList = getStockDealDayEntityList(
                stockEntity.getId()
        );
        if (null == stockDealDayEntityList || stockDealDayEntityList.isEmpty()) {
            return;
        }
        for (StockDealDayEntity stockDealDayEntity : stockDealDayEntityList) {
            if (null == stockDealDayEntity) {
                continue;
            }
            //忽略当日无交易的脏数据
            if (null != stockDealDayEntity.getClosePrice()
                    && 0 == BigDecimal.ZERO.compareTo(stockDealDayEntity.getClosePrice())) {
                continue;
            }
            try {
                while (null == weekStartDate || null == weekEndDate
                        || DateUtil.compare(weekEndDate, stockDealDayEntity.getDt(), DateUtil.DATE_FORMAT_1)) {
                    changeWeek = true;
                    if (weekPos > weekList.size() - 1) {
                        break;
                    }
                    weekStartDate = weekList.get(weekPos);
                    weekEndDate = weekList.get(weekPos + 1);
                    weekPos += 2;
                }
                if (changeWeek) {
                    changeWeek = false;
                    stockDealWeekEntity = new StockDealWeekEntity();
                    stockDealWeekEntity.setStockId(stockEntity.getId());
                    if (null != closePrice) {
                        stockDealWeekEntity.setDt(dt);
                        stockDealWeekEntity.setClosePrice(closePrice);
                        stockDealWeekEntity.setDealNum(dealNum);
                        stockDealWeekEntity.setDealMoney(dealMoney);
                        stockDealWeekEntity.setCircEquity(circEquity);
                        stockDealWeekEntity.setTotalEquity(totalEquity);
                        stockDealWeekEntityList.add(stockDealWeekEntity);
                    }
                    closePrice = null;
                    dealMoney = null;
                    dealNum = null;
                    circEquity = null;
                    totalEquity = null;
                }
                dt = stockDealDayEntity.getDt();
                closePrice = null == closePrice || (
                        null != stockDealDayEntity.getClosePrice()
                                && 0 > BigDecimal.ZERO.compareTo(stockDealDayEntity.getClosePrice())
                ) ? stockDealDayEntity.getClosePrice() : closePrice;
                dealMoney = null == dealMoney ?
                        stockDealDayEntity.getDealMoney() : dealMoney.add(stockDealDayEntity.getDealMoney());
                dealNum = null == dealNum ?
                        stockDealDayEntity.getDealNum() : dealNum + stockDealDayEntity.getDealNum();
                circEquity = stockDealDayEntity.getCircEquity();
                totalEquity = stockDealDayEntity.getTotalEquity();
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }
        }
        //处理最后一周的数据
        if (null != closePrice) {
            stockDealWeekEntity = new StockDealWeekEntity();
            stockDealWeekEntity.setStockId(stockEntity.getId());
            stockDealWeekEntity.setDt(dt);
            stockDealWeekEntity.setClosePrice(closePrice);
            stockDealWeekEntity.setDealNum(dealNum);
            stockDealWeekEntity.setDealMoney(dealMoney);
            stockDealWeekEntity.setCircEquity(circEquity);
            stockDealWeekEntity.setTotalEquity(totalEquity);
            stockDealWeekEntityList.add(stockDealWeekEntity);
        }
        if (!stockDealWeekEntityList.isEmpty()) {
            if (syncTotal) {
                stockDealWeekMapper.batchInsert(stockDealWeekEntityList);
            } else {
                if (null != stockDealWeekEntity) {
                    List<StockDealWeekEntity> dbStockDealWeekEntityList = stockDealWeekMapper.getByDate(
                            stockEntity.getId(), currentWeekStartDate, currentWeekEndDate
                    );
                    if (null != dbStockDealWeekEntityList && !dbStockDealWeekEntityList.isEmpty()) {
                        dbStockDealWeekEntity = dbStockDealWeekEntityList.get(
                                dbStockDealWeekEntityList.size() - 1
                        );
                        stockDealWeekEntity.setId(dbStockDealWeekEntity.getId());
                        stockDealWeekMapper.update(stockDealWeekEntity);
                    } else {
                        stockDealWeekMapper.insert(stockDealWeekEntity);
                    }
                }
            }
        }
        stockDealDayEntityList.clear();
        stockDealWeekEntityList.clear();
    }


    /**
     * 处理单只股票
     *
     * @param stockEntity
     */
    @Override
    public void handle(StockEntity stockEntity) {
        if (null == stockEntity || null == stockEntity.getId() || stockEntity.getId() < 0) {
            return;
        }
        syncPrice(stockEntity);
        syncDeal(stockEntity);
    }

    /**
     * 同步所有的按周交易信息数据
     */
    @LogShowTimeAnt
    public void syncTotalDealWeekInfo() {
        syncTotal = true;
        try {
            Map<String, String> dateScopeMap = new HashMap<>(2);
            dateScopeMap.put("startDate", "1990-12-01");
            dateScopeMap.put("endDate", DateUtil.getCurrentDate());
            weekList = doDateByStatisticsType(dateType, dateScopeMap);
            this.dropShadow();
            this.createShadow();
            aStockMarketTopIndexScan(this);
            aStockMarketScan(this);
            this.shadowConvert();
            this.dropShadow();
            this.optimizeTable();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 同步交易当周信息数据
     */
    @LogShowTimeAnt
    public void syncCurrentDealWeekInfo() {
        syncTotal = false;
        try {
            Map<String, String> dateScopeMap = new HashMap<>(2);
            currentWeekEndDate = DateUtil.getCurrentDate();
            Integer weekNum = DateUtil.getDayInWeekNum(currentWeekEndDate, DateUtil.DATE_FORMAT_1);
            currentWeekStartDate = DateUtil.getRelateDate(
                    currentWeekEndDate, 0, 0, 0 == weekNum ? -6 : 1 - weekNum, DateUtil.DATE_FORMAT_1
            );
            dateScopeMap.put("startDate", currentWeekStartDate);
            dateScopeMap.put("endDate", currentWeekEndDate);
            weekList = doDateByStatisticsType(dateType, dateScopeMap);
            aStockMarketTopIndexScan(this);
            aStockMarketScan(this);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
