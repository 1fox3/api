package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.mapper.StockMapper;
import com.fox.api.property.stock.StockProperty;
import com.fox.api.service.stock.StockUtilService;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
     * 主要处理的信息股市，沪，深，已沪为例
     */
    protected String mainStockMarket = "sh";

    /**
     * 主要处理的信息股市id
     */
    protected Integer mainStockMarketId = 1;

    /**
     * 今天是否为交易日
     * @return
     */
    protected Boolean todayIsDealDate()
    {
        return StockUtil.todayIsDealDate(mainStockMarket);
    }

    /**
     * 判定当前是否为交易时间
     * @return
     */
    protected Boolean isDealTime()
    {
        return StockUtil.isDealTime(mainStockMarket);
    }
}
