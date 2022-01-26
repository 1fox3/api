package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.mapper.StockInfoMapper;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.schedule.stock.handler.StockScheduleBatchHandler;
import com.fox.api.schedule.stock.handler.StockScheduleCacheBatchCodeHandler;
import com.fox.api.schedule.stock.handler.StockScheduleCacheBatchHandler;
import com.fox.api.schedule.stock.handler.StockScheduleHandler;
import com.fox.api.service.admin.DateTypeService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.constant.StockMarketStatusConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fox.spider.stock.constant.StockConst.*;

/**
 * 计划任务基类
 *
 * @author lusongsong
 * @date 2020/3/27 17:56
 */
public class StockBaseSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 缓存key修改前缀
     */
    protected String cacheNamePre = "pre";
    @Autowired
    protected StockMapper stockMapper;

    @Autowired
    protected StockInfoMapper stockInfoMapper;

    @Value("${redis.stock.stock.list}")
    protected String redisStockList;

    @Value("${redis.stock.stock.code-list}")
    protected String redisStockCodeList;

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

    @Value("${redis.stock.realtime.stock.rank.up-limit.list}")
    protected String stockRealtimeStockRankUpLimitList;

    @Value("${redis.stock.realtime.stock.rank.down-limit.list}")
    protected String stockRealtimeStockRankDownLimitList;

    @Autowired
    protected StockRedisUtil stockRedisUtil;

    /**
     * 日期类型
     */
    @Autowired
    DateTypeService dateTypeService;
    /**
     * 实时交易任务是否有执行下一次权限
     */
    protected static final String REALTIME_SCHEDULE_HAS_NEXT = "rtScheduleHasNext";

    public final Map<Integer, StockVo> demoStock = new HashMap<Integer, StockVo>() {{
        //贵州茅台
        put(SM_SH, new StockVo("600519", SM_SH));
        //同花顺
        put(SM_SZ, new StockVo("300033", SM_SZ));
        //腾讯控股
        put(SM_HK, new StockVo("00700", SM_HK));
    }};

    /**
     * 判断日期是否为一个交易日
     *
     * @param stockMarket
     * @param dealDate
     * @return
     */
    public Boolean isDealDate(Integer stockMarket, String dealDate) {
        try {
            Integer dayInWeekNum = DateUtil.getDayInWeekNum(dealDate, DateUtil.DATE_FORMAT_1);
            if (1 <= dayInWeekNum && 5 >= dayInWeekNum) {
                Integer dateType = dateTypeService.getByDate(dealDate);
                if (stockMarket.equals(SM_HK)) {
                    if (DateTypeService.DATE_TYPE_WORKDAY == dateType
                            || DateTypeService.DATE_TYPE_WEEKEND == dateType) {
                        return true;
                    }
                } else {
                    if (DateTypeService.DATE_TYPE_WORKDAY == dateType) {
                        return true;
                    }
                }
            }
        } catch (ParseException e) {
            logger.error(dealDate);
            logger.error(e.getMessage());
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
                List<StockEntity> stockEntityList = this.stockMapper.getListByType(
                        StockConst.ST_STOCK,
                        stockId,
                        stockMarket,
                        null,
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
     * 批量处理股票
     *
     * @param stockMarket
     * @param stockScheduleBatchHandler
     */
    public void stockMarketBatchScan(Integer stockMarket, StockScheduleBatchHandler stockScheduleBatchHandler) {
        Integer startId = 0;
        Integer onceLimit = 300;
        Integer stockStatus = 0;
        try {
            while (true) {
                List<StockEntity> stockEntityList = this.stockMapper.getListByType(
                        StockConst.ST_STOCK,
                        startId,
                        stockMarket,
                        stockStatus,
                        onceLimit.toString()
                );
                if (null == stockEntityList || stockEntityList.isEmpty()) {
                    break;
                }
                startId = stockEntityList.get(stockEntityList.size() - 1).getId();
                stockScheduleBatchHandler.batchHandle(stockEntityList);
                if (stockEntityList.size() < onceLimit) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(stockMarket), e);
        }
    }

    /**
     * 缓存中的信息批量过滤
     *
     * @param stockMarket
     * @param stockScheduleCacheBatchHandler
     */
    public void stockMarketCacheBatchScan(Integer stockMarket, StockScheduleCacheBatchHandler stockScheduleCacheBatchHandler) {
        try {
            String listCacheKey = redisStockList + ":" + stockMarket;
            Long stockListSize = stockRedisUtil.lSize(listCacheKey);
            Integer onceLimit = 200;
            if (stockListSize > 0) {
                for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
                    List<StockVo> stockVoList = (List<StockVo>) (List) stockRedisUtil.lRange(listCacheKey, i, i + onceLimit - 1);
                    if (null == stockVoList || stockVoList.isEmpty()) {
                        break;
                    }
                    stockScheduleCacheBatchHandler.cacheBatchHandle(stockVoList);
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(stockMarket), e);
        }
    }

    /**
     * 缓存中的信息批量过滤
     *
     * @param stockMarket
     * @param stockScheduleCacheBatchCodeHandler
     */
    public void stockMarketCacheBatchCodeScan(Integer stockMarket,
                                              StockScheduleCacheBatchCodeHandler stockScheduleCacheBatchCodeHandler) {
        try {
            String stockCodeListCacheKey = redisStockCodeList + ":" + stockMarket;
            Long stockListSize = stockRedisUtil.lSize(stockCodeListCacheKey);
            Integer onceLimit = 200;
            if (stockListSize > 0) {
                for (Long i = Long.valueOf(0); i < stockListSize; i += onceLimit) {
                    List<String> stockCodeList = (List<String>) (List) stockRedisUtil.lRange(
                            stockCodeListCacheKey, i, i + onceLimit - 1
                    );
                    if (null == stockCodeList || stockCodeList.isEmpty()) {
                        break;
                    }
                    stockScheduleCacheBatchCodeHandler.cacheBatchCodeHandle(stockCodeList);
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(stockMarket), e);
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
     * 获取周月范围列表
     *
     * @param statisticsType
     * @param map
     * @return
     * @throws ParseException
     */
    public List<String> doDateByStatisticsType(String statisticsType, Map<String, String> map) throws ParseException {
        List<String> listWeekOrMonth = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat(DateUtil.DATE_FORMAT_1);
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

    /**
     * 交易相关实时任务是可以执行
     *
     * @param stockMarket
     * @param schedule
     * @return
     */
    public Boolean realtimeDealScheduleCanRun(Integer stockMarket, String schedule) {
        if (null == stockMarket || !StockConst.SM_ALL.contains(stockMarket) || null == schedule || schedule.isEmpty()) {
            return false;
        }

        Integer smStatus = StockUtil.smDealStatus(stockMarket);
        if (null == smStatus) {
            return false;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(REALTIME_SCHEDULE_HAS_NEXT);
        stringBuffer.append(":");
        stringBuffer.append(schedule);
        stringBuffer.append(":");
        stringBuffer.append(stockMarket);
        String hasNextKey = stringBuffer.toString();

        if (StockMarketStatusConst.CAN_DEAL_STATUS_LIST.contains(smStatus)) {
            stockRedisUtil.set(hasNextKey, 1, 36000L);
            return true;
        }

        if (stockRedisUtil.get(hasNextKey).equals(1)) {
            stockRedisUtil.set(hasNextKey, 0, 36000L);
            return true;
        }

        return false;
    }

    /**
     * 列表转换
     *
     * @param stockEntityList
     * @return
     */
    public static List<StockVo> stockListConvert(List<StockEntity> stockEntityList) {
        if (null != stockEntityList) {
            List<StockVo> stockVoList = new ArrayList<>(stockEntityList.size());
            for (StockEntity stockEntity : stockEntityList) {
                if (null != stockEntity && stockEntity instanceof StockEntity) {
                    stockVoList.add(new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket()));
                }
            }
            return stockVoList;
        }
        return null;
    }
}
