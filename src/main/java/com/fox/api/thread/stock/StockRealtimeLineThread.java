package com.fox.api.thread.stock;

import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.nets.api.NetsStockBaseApi;
import com.fox.api.util.StockUtil;
import com.fox.api.util.redis.StockRedisUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StockRealtimeLineThread extends Thread {
    protected StockRedisUtil stockRedisUtil;

    protected String redisStockList;

    protected String redisRealtimeStockLineHash;

    private StockRealtimeService stockRealtimeService;

    //起始位置
    private Long startId;
    //结束位置
    private Long endId;
    //当前处理的位置
    private Long currentId;

    public StockRealtimeLineThread(
            StockRedisUtil stockRedisUtil,
            StockRealtimeService stockRealtimeService,
            String redisRealtimeStockLineHash,
            String redisStockList,
            Long startId,
            Long endId
    ) {
        this.stockRedisUtil = stockRedisUtil;
        this.stockRealtimeService = stockRealtimeService;
        this.redisRealtimeStockLineHash = redisRealtimeStockLineHash;
        this.redisStockList = redisStockList;
        this.startId = startId;
        this.currentId = startId;
        this.endId = endId;
    }

    /**
     * 任务是否已执行完成
     * @return
     */
    public Boolean isComplete() {
        return this.currentId >= this.endId ? true : false;
    }
    /**
     * 线程任务，获取股票实时数据并放入到缓存中
     */
    public void run() {
        Integer threadOnceLimit = 200;
        NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
        for (Long i = this.startId; i < this.endId + 1; i += threadOnceLimit - 1) {
            List<Object> stockEntityList = this.stockRedisUtil.lRange(
                    this.redisStockList,
                    Long.valueOf(i),
                    Long.valueOf(i + threadOnceLimit - 1)
            );
            if (null != stockEntityList || 0 < stockEntityList.size()) {
                Map<String, StockRealtimeLinePo> lineMap = new LinkedHashMap<>();
                for (Object stockEntity : stockEntityList) {
                    Integer stockId = ((StockEntity)stockEntity).getId();
                    StockRealtimeLinePo stockRealtimeLinePo =
                            netsMinuteRealtime.getRealtimeData(
                                    NetsStockBaseApi.getNetsStockInfoMap((StockEntity)stockEntity)
                            );
                    if (null != stockId && null != stockRealtimeLinePo) {
                        lineMap.put(String.valueOf(stockId), stockRealtimeLinePo);
                    }
                }
                if (0 < lineMap.size()) {
                    this.stockRedisUtil.hPutAll(this.redisRealtimeStockLineHash, lineMap);
                }
            }
            this.currentId = i + threadOnceLimit - 1;
        }
    }
}
