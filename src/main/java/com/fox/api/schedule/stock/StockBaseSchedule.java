package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.service.admin.DateTypeService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.redis.StockRedisUtil;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 计划任务基类
 *
 * @author lusongsong
 * @date 2020/3/27 17:56
 */
public class StockBaseSchedule {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected StockMapper stockMapper;

    @Autowired
    protected StockInfoMapper stockInfoMapper;

    protected int stockType = 2;

    @Value("${redis.stock.stock.list}")
    protected String redisStockList;

    @Value("${redis.stock.stock.hash}")
    protected String redisStockHash;

    @Value("${redis.stock.stock.id-list}")
    protected String redisStockIdList;

    @Value("${redis.stock.realtime.stock.info.hash}")
    protected String redisRealtimeStockInfoHash;

    @Value("${redis.stock.realtime.stock.line.hash}")
    protected String redisRealtimeStockLineHash;

    @Value("${redis.stock.realtime.stock.rank.price}")
    protected String redisRealtimeRankPriceZSet;

    @Value("${redis.stock.realtime.stock.rank.uptick}")
    protected String redisRealtimeRankUptickRateZSet;

    @Value("${redis.stock.realtime.stock.rank.surge}")
    protected String redisRealtimeRankSurgeRateZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.num}")
    protected String redisRealtimeRankDealNumZSet;

    @Value("${redis.stock.realtime.stock.rank.deal.money}")
    protected String redisRealtimeRankDealMoneyZSet;

    @Value("${redis.stock.realtime.stock.rank.stop}")
    protected String stockRealtimeStockStopStatistics;

    @Value("${redis.stock.realtime.stock.rank.uptick-statistics}")
    protected String stockRealtimeStockUptickRateStatistics;

    @Autowired
    protected StockRedisUtil stockRedisUtil;

    /**
     * 日期类型
     */
    @Autowired
    DateTypeService dateTypeService;

    /**
     * 判断日期是否为一个交易日
     *
     * @param stockMarket
     * @param dealDate
     * @return
     * @throws ParseException
     */
    public Boolean isDealDate(Integer stockMarket, String dealDate) throws ParseException {
        Integer dayInWeekNum = DateUtil.getDayInWeekNum(dealDate, DateUtil.DATE_FORMAT_1);
        if (1 <= dayInWeekNum && 5 >= dayInWeekNum) {
            Integer dateType = dateTypeService.getByDate(dealDate);
            if (stockMarket.equals(StockConst.SM_HK)) {
                if (DateTypeService.DATE_TYPE_WORKDAY.equals(dateType)
                        || DateTypeService.DATE_TYPE_WEEKEND.equals(dateType)) {
                    return true;
                }
            } else {
                if (DateTypeService.DATE_TYPE_WORKDAY.equals(dateType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 遍历所有股票
     *
     * @param stockScheduleHandler
     */
    public void allStockMarketScan(StockScheduleHandler stockScheduleHandler) {
        stockMarketListScan(StockConst.SM_ALL, stockScheduleHandler);
    }

    /**
     * 遍历A股
     *
     * @param stockScheduleHandler
     */
    public void aStockMarketScan(StockScheduleHandler stockScheduleHandler) {
        stockMarketListScan(StockConst.SM_A_LIST, stockScheduleHandler);
    }

    /**
     * 遍历集市列表处理
     *
     * @param stockMarketList
     * @param stockScheduleHandler
     */
    public void stockMarketListScan(List<Integer> stockMarketList, StockScheduleHandler stockScheduleHandler) {
        for (Integer stockMarket : stockMarketList) {
            stockMarketScan(stockMarket, stockScheduleHandler);
        }
    }

    /**
     * 遍历单集市股票
     *
     * @param stockMarket
     * @param stockScheduleHandler
     */
    public void stockMarketScan(Integer stockMarket, StockScheduleHandler stockScheduleHandler) {
        Integer stockId = 0;
        Integer onceLimit = 300;
        try {
            while (true) {
                List<StockEntity> stockEntityList = this.stockMapper.getTotalByType(
                        2,
                        stockId,
                        stockMarket,
                        onceLimit.toString()
                );
                if (null == stockEntityList || stockEntityList.isEmpty()) {
                    break;
                }
                for (StockEntity stockEntity : stockEntityList) {
                    if (null == stockEntity) {
                        continue;
                    }
                    stockId = null == stockEntity.getId() ? stockId + 1 : stockEntity.getId();
                    try {
                        if (null != stockEntity.getId()) {
                            stockScheduleHandler.handle(stockEntity);
                        }
                    } catch (Exception e) {
                        logger.error(Integer.toString(stockId));
                        logger.error(e.getMessage());
                    }
                }
                if (stockEntityList.size() < onceLimit) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(Integer.toString(stockId));
            logger.error(e.getMessage());
        }
    }

    /**
     * 遍历A股TOP指数
     *
     * @param stockScheduleHandler
     */
    public void aStockMarketTopIndexScan(StockScheduleHandler stockScheduleHandler) {
        stockMarketListTopIndexScan(StockConst.SM_A_LIST, stockScheduleHandler);
    }

    /**
     * 遍历集市TOP指数处理
     *
     * @param stockMarketList
     * @param stockScheduleHandler
     */
    public void stockMarketListTopIndexScan(List<Integer> stockMarketList, StockScheduleHandler stockScheduleHandler) {
        for (Integer stockMarket : stockMarketList) {
            stockMarketTopIndexScan(stockMarket, stockScheduleHandler);
        }
    }

    /**
     * 遍历单集市TOP指数
     *
     * @param stockMarket
     * @param stockScheduleHandler
     */
    public void stockMarketTopIndexScan(Integer stockMarket, StockScheduleHandler stockScheduleHandler) {
        try {
            List<StockVo> topIndexList = StockConst.stockMarketTopIndex(stockMarket);
            for (StockVo stockVo : topIndexList) {
                StockEntity stockEntity = this.stockMapper.getByStockCode(
                        stockVo.getStockCode(),
                        stockVo.getStockMarket()
                );
                if (null != stockEntity && null != stockEntity.getId()) {
                    stockScheduleHandler.handle(stockEntity);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 遍历全部股票
     *
     * @param stockScheduleHandler
     */
    public void totalStockScan(StockScheduleHandler stockScheduleHandler) {
        Integer stockId = 0;
        Integer limit = 500;
        while (true) {
            try {
                List<StockEntity> stockEntityList = stockMapper.getListById(stockId, limit);
                if (null == stockEntityList || stockEntityList.isEmpty()) {
                    break;
                }

                for (StockEntity stockEntity : stockEntityList) {
                    if (null != stockEntity && null != stockEntity.getId()) {
                        stockId = stockEntity.getId();
                        stockScheduleHandler.handle(stockEntity);
                    } else {
                        stockId++;
                    }
                }

                if (stockEntityList.size() < limit) {
                    break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                break;
            }
        }
    }

    /**
     * 获取周月范围列表
     *
     * @param statisticsType
     * @param map
     * @return
     * @throws ParseException
     */
    public List<String> doDateByStatisticsType(String statisticsType, Map<String, String> map) throws ParseException {
        List<String> listWeekOrMonth = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        Date sDate = dateFormat.parse(startDate);
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        sCalendar.setTime(sDate);
        Date eDate = dateFormat.parse(endDate);
        Calendar eCalendar = Calendar.getInstance();
        eCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        eCalendar.setTime(eDate);
        boolean bool = true;
        if (statisticsType.equals("week")) {
            while (sCalendar.getTime().getTime() < eCalendar.getTime().getTime()) {
                if (bool || sCalendar.get(Calendar.DAY_OF_WEEK) == 2 || sCalendar.get(Calendar.DAY_OF_WEEK) == 1) {
                    listWeekOrMonth.add(dateFormat.format(sCalendar.getTime()));
                    bool = false;
                }
                sCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            if (listWeekOrMonth.size() % 2 != 0) {
                listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            }
        } else {
            while (sCalendar.getTime().getTime() < eCalendar.getTime().getTime()) {
                if (bool || sCalendar.get(Calendar.DAY_OF_MONTH) == 1 || sCalendar.get(Calendar.DAY_OF_MONTH) == sCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    listWeekOrMonth.add(dateFormat.format(sCalendar.getTime()));
                    bool = false;
                }
                sCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            if (listWeekOrMonth.size() % 2 != 0) {
                listWeekOrMonth.add(dateFormat.format(eCalendar.getTime()));
            }
        }

        return listWeekOrMonth;
    }
}
