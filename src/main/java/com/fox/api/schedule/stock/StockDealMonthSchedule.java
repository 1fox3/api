package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.stock.entity.*;
import com.fox.api.dao.stock.mapper.StockDealDayMapper;
import com.fox.api.dao.stock.mapper.StockDealMonthMapper;
import com.fox.api.dao.stock.mapper.StockPriceDayMapper;
import com.fox.api.dao.stock.mapper.StockPriceMonthMapper;
import com.fox.api.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 股票月粒度交易数据同步
 * @author lusongsong
 * @date 2020/10/20 21:24
 */
@Component
public class StockDealMonthSchedule extends StockBaseSchedule implements StockScheduleHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    StockPriceMonthMapper stockPriceMonthMapper;
    @Autowired
    StockDealMonthMapper stockDealMonthMapper;
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
    private String dateType = "month";
    /**
     * 日期范围列表
     */
    private List<String> monthList;
    /**
     * 当前周开始时间
     */
    private String currentMonthStartDate;
    /**
     * 当前周结束时间
     */
    private String currentMonthEndDate;
    /**
     * 创建影子表
     */
    private void createShadow() {
        stockPriceMonthMapper.createShadow();
        stockDealMonthMapper.createShadow();
    }

    /**
     * 数据表重命名
     */
    private void shadowConvert() {
        stockPriceMonthMapper.shadowConvert();
        stockDealMonthMapper.shadowConvert();
    }

    /**
     * 删除影子表
     */
    private void dropShadow() {
        stockPriceMonthMapper.dropShadow();
        stockDealMonthMapper.dropShadow();
    }

    /**
     * 数据表重命名
     */
    private void optimizeTable() {
        stockPriceMonthMapper.optimize();
        stockDealMonthMapper.optimize();
    }

    /**
     * 查询价格列表
     * @param fqType
     * @param stockId
     * @return
     */
    private List<StockPriceDayEntity> getStockPriceDayEntityList(Integer fqType, Integer stockId) {
        return syncTotal ? stockPriceDayMapper.getTotalByStock(fqType, stockId)
                : stockPriceDayMapper.getByDate(fqType, stockId, currentMonthStartDate, currentMonthEndDate);
    }

    /**
     * 查询交易列表
     * @param stockId
     * @return
     */
    private List<StockDealDayEntity> getStockDealDayEntityList(Integer stockId) {
        return syncTotal ? stockDealDayMapper.getTotalByStock(stockId)
                : stockDealDayMapper.getByDate(stockId, currentMonthStartDate, currentMonthEndDate);
    }

    /**
     * 同步价格
     * @param stockEntity
     */
    private void syncPrice(StockEntity stockEntity) {
        StockPriceMonthEntity stockPriceMonthEntity = null, dbStockPriceMonthEntity;
        List<StockPriceMonthEntity> stockPriceMonthEntityList = new LinkedList<>();
        for (Integer fqType : fqTypeList) {
            List<StockPriceDayEntity> stockPriceDayEntityList = getStockPriceDayEntityList(
                    fqType, stockEntity.getId()
            );
            if (null == stockPriceDayEntityList || stockPriceDayEntityList.isEmpty()) {
                continue;
            }
            Integer monthPos = 0;
            Boolean changeMonth = false;
            String dt = null, monthStartDate = null, monthEndDate = null;
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
                    while (null == monthStartDate || null == monthEndDate
                            || DateUtil.compare(monthEndDate, stockPriceDayEntity.getDt(), DateUtil.DATE_FORMAT_1) <= 0) {
                        changeMonth = true;
                        if (monthPos > monthList.size() - 1) {
                            break;
                        }
                        monthStartDate = monthList.get(monthPos);
                        monthEndDate = monthList.get(monthPos + 1);
                        monthPos += 2;
                    }
                    if (changeMonth) {
                        changeMonth = false;
                        stockPriceMonthEntity = new StockPriceMonthEntity();
                        stockPriceMonthEntity.setFqType(fqType);
                        stockPriceMonthEntity.setStockId(stockEntity.getId());
                        if (null != closePrice) {
                            stockPriceMonthEntity.setDt(dt);
                            stockPriceMonthEntity.setOpenPrice(openPrice);
                            stockPriceMonthEntity.setClosePrice(closePrice);
                            stockPriceMonthEntity.setHighestPrice(highestPrice);
                            stockPriceMonthEntity.setLowestPrice(lowestPrice);
                            stockPriceMonthEntity.setPreClosePrice(preClosePrice);
                            stockPriceMonthEntityList.add(stockPriceMonthEntity);
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
            //处理最后一个月的数据
            if (null != closePrice) {
                stockPriceMonthEntity = new StockPriceMonthEntity();
                stockPriceMonthEntity.setFqType(fqType);
                stockPriceMonthEntity.setStockId(stockEntity.getId());
                stockPriceMonthEntity.setDt(dt);
                stockPriceMonthEntity.setOpenPrice(openPrice);
                stockPriceMonthEntity.setClosePrice(closePrice);
                stockPriceMonthEntity.setHighestPrice(highestPrice);
                stockPriceMonthEntity.setLowestPrice(lowestPrice);
                stockPriceMonthEntity.setPreClosePrice(preClosePrice);
                stockPriceMonthEntityList.add(stockPriceMonthEntity);
            }
            if (!stockPriceMonthEntityList.isEmpty()) {
                if (syncTotal) {
                    stockPriceMonthMapper.batchInsert(stockPriceMonthEntityList);
                } else {
                    if (null != stockPriceMonthEntity) {
                        List<StockPriceMonthEntity> dbStockPriceMonthEntityList = stockPriceMonthMapper.getTotalByStock(
                                fqType, stockEntity.getId()
                        );
                        if (null != dbStockPriceMonthEntityList && !dbStockPriceMonthEntityList.isEmpty()) {
                            dbStockPriceMonthEntity = dbStockPriceMonthEntityList.get(
                                    dbStockPriceMonthEntityList.size() - 1
                            );
                            String lastDt = dbStockPriceMonthEntity.getDt();
                            try {
                                stockPriceMonthEntity.setPreClosePrice(dbStockPriceMonthEntity.getClosePrice());
                                if (DateUtil.compare(lastDt, currentMonthStartDate, DateUtil.DATE_FORMAT_1) <= 0) {
                                    stockPriceMonthMapper.insert(stockPriceMonthEntity);
                                } else {
                                    stockPriceMonthEntity.setId(dbStockPriceMonthEntity.getId());
                                    stockPriceMonthMapper.update(stockPriceMonthEntity);
                                }
                            } catch (ParseException e) {
                                logger.error(e.getMessage());
                            }
                        } else {
                            stockPriceMonthMapper.insert(stockPriceMonthEntity);
                        }
                    }
                }
            }
            stockPriceMonthEntityList.clear();
            stockPriceDayEntityList.clear();
        }
    }



    /**
     * 同步交易
     * @param stockEntity
     */
    private void syncDeal(StockEntity stockEntity) {
        StockDealMonthEntity stockDealMonthEntity = null, dbStockDealMonthEntity;
        List<StockDealMonthEntity> stockDealMonthEntityList = new LinkedList<>();
        Integer monthPos = 0;
        Boolean changeMonth = false;
        String dt = null, monthStartDate = null, monthEndDate = null;
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
                while (null == monthStartDate || null == monthEndDate
                        || DateUtil.compare(monthEndDate, stockDealDayEntity.getDt(), DateUtil.DATE_FORMAT_1) <= 0) {
                    changeMonth = true;
                    if (monthPos > monthList.size() - 1) {
                        break;
                    }
                    monthStartDate = monthList.get(monthPos);
                    monthEndDate = monthList.get(monthPos + 1);
                    monthPos += 2;
                }
                if (changeMonth) {
                    changeMonth = false;
                    stockDealMonthEntity = new StockDealMonthEntity();
                    stockDealMonthEntity.setStockId(stockEntity.getId());
                    if (null != closePrice) {
                        stockDealMonthEntity.setDt(dt);
                        stockDealMonthEntity.setClosePrice(closePrice);
                        stockDealMonthEntity.setDealNum(dealNum);
                        stockDealMonthEntity.setDealMoney(dealMoney);
                        stockDealMonthEntity.setCircEquity(circEquity);
                        stockDealMonthEntity.setTotalEquity(totalEquity);
                        stockDealMonthEntityList.add(stockDealMonthEntity);
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
        //处理最后一个月的数据
        if (null != closePrice) {
            stockDealMonthEntity = new StockDealMonthEntity();
            stockDealMonthEntity.setStockId(stockEntity.getId());
            stockDealMonthEntity.setDt(dt);
            stockDealMonthEntity.setClosePrice(closePrice);
            stockDealMonthEntity.setDealNum(dealNum);
            stockDealMonthEntity.setDealMoney(dealMoney);
            stockDealMonthEntity.setCircEquity(circEquity);
            stockDealMonthEntity.setTotalEquity(totalEquity);
            stockDealMonthEntityList.add(stockDealMonthEntity);
        }
        if (!stockDealMonthEntityList.isEmpty()) {
            if (syncTotal) {
                stockDealMonthMapper.batchInsert(stockDealMonthEntityList);
            } else {
                if (null != stockDealMonthEntity) {
                    List<StockDealMonthEntity> dbStockDealMonthEntityList = stockDealMonthMapper.getByDate(
                            stockEntity.getId(), currentMonthStartDate, currentMonthEndDate
                    );
                    if (null != dbStockDealMonthEntityList && !dbStockDealMonthEntityList.isEmpty()) {
                        dbStockDealMonthEntity = dbStockDealMonthEntityList.get(
                                dbStockDealMonthEntityList.size() - 1
                        );
                        stockDealMonthEntity.setId(dbStockDealMonthEntity.getId());
                        stockDealMonthMapper.update(stockDealMonthEntity);
                    } else {
                        stockDealMonthMapper.insert(stockDealMonthEntity);
                    }
                }
            }
        }
        stockDealDayEntityList.clear();
        stockDealMonthEntityList.clear();
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
    public void syncTotalDealMonthInfo() {
        syncTotal = true;
        try {
            Map<String, String> dateScopeMap = new HashMap<>(2);
            dateScopeMap.put("startDate", "1990-12-01");
            dateScopeMap.put("endDate", DateUtil.getCurrentDate());
            monthList = doDateByStatisticsType(dateType, dateScopeMap);
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
    public void syncCurrentDealMonthInfo() {
        syncTotal = false;
        try {
            Map<String, String> dateScopeMap = new HashMap<>(2);
            currentMonthEndDate = DateUtil.getCurrentDate();
            currentMonthStartDate = currentMonthEndDate.substring(0, 8) + "01";
            dateScopeMap.put("startDate", currentMonthStartDate);
            dateScopeMap.put("endDate", currentMonthEndDate);
            monthList = doDateByStatisticsType(dateType, dateScopeMap);
            aStockMarketTopIndexScan(this);
            aStockMarketScan(this);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
