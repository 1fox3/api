package com.fox.api.schedule.stock;

import com.fox.api.entity.po.stock.api.StockRealtimeDealInfoPo;
import com.fox.api.schedule.stock.handler.StockScheduleCacheBatchHandler;
import com.fox.api.service.stock.api.request.StockRealtimeDealInfoApiService;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 批量获取股票的实时信息数据
 *
 * @author lusongsong
 * @date 2020/3/27 17:52
 */
@Component
public class StockRealtimeInfoSchedule extends StockBaseSchedule implements StockScheduleCacheBatchHandler {
    /**
     * 获取股票最新交易日交易信息接口类
     */
    @Autowired
    StockRealtimeDealInfoApiService stockRealtimeDealInfoApiService;
    /**
     * 当前股票最新交易日交易信息缓存key
     */
    private String stockRealtimeDealInfoCacheKey = "";

    /**
     * 更新股票实时交易数据
     */
    public void syncStockRealtimeDealInfo() {
        String schedule = "StockRealtimeInfoSchedule:syncStockRealtimeDealInfo";
        for (Integer stockMarket : StockConst.SM_CODE_ALL) {
            if (!realtimeDealScheduleCanRun(stockMarket, schedule)) {
                continue;
            }
            stockRealtimeDealInfoCacheKey = redisRealtimeStockInfoHash + ":" + stockMarket;
            stockMarketCacheBatchScan(stockMarket, this);
        }
    }

    /**
     * 计划任务批量处理股票
     *
     * @param stockVoList
     */
    @Override
    public void cacheBatchHandle(List<StockVo> stockVoList) {
        if (null == stockVoList || stockVoList.isEmpty()) {
            return;
        }
        Map<String, StockRealtimeDealInfoPo> stringStockRealtimeDealInfoPoMap =
                stockRealtimeDealInfoApiService.batchRealtimeDealInfo(stockVoList);
        if (null != stringStockRealtimeDealInfoPoMap) {
            stockRedisUtil.hPutAll(stockRealtimeDealInfoCacheKey, stringStockRealtimeDealInfoPoMap);
        }
    }
}
