package com.fox.api.schedule.stock;

import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.property.stock.StockProperty;
import com.fox.api.service.admin.DateTypeService;
import com.fox.api.service.stock.StockUtilService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.util.List;

/**
 * 计划任务基类
 * @author lusongsong
 */
public class StockBaseSchedule {
    @Autowired
    protected StockMapper stockMapper;

    @Autowired
    protected StockProperty stockProperty;

    @Value("${stock.type.stock.stock-type}")
    protected int stockType;

    @Value("${stock.market.sh.stock-market}")
    protected int shStockMarket;

    @Value("${stock.market.sz.stock-market}")
    protected int szStockMarket;

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

    @Autowired
    protected StockUtilService stockUtilService;

    @Autowired
    protected StockProperty stockConfig;

    /**
     * 日期类型
     */
    @Autowired
    DateTypeService dateTypeService;

    /**
     * 判断日期是否为一个交易日
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
}
