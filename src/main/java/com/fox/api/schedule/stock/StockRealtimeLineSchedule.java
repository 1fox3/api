package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.thread.stock.StockRealtimeLineThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 批量获取股票的实时线图数据
 * @author lusongsong
 * @date 2020/3/27 20:31
 */
@Component
public class StockRealtimeLineSchedule extends StockBaseSchedule {
    @Autowired
    private StockRealtimeService stockRealtimeService;

    /**
     * 获取实时交易线图信息
     */
    @LogShowTimeAnt
    public void stockRealtimeLine() {
        //需要开启的线程数量
        Integer threadNum = 50;
        Long stockListSize = this.stockRedisUtil.lSize(this.redisStockList);
        Integer threadOnceLimit = (int) (stockListSize / threadNum) + 1;
        List<StockRealtimeLineThread> threadList = new LinkedList<>();
        for (Integer i = 0; i < threadNum; i += 1) {
            StockRealtimeLineThread stockRealtimeLineThread = new StockRealtimeLineThread(
                    this.stockRedisUtil,
                    this.stockRealtimeService,
                    this.redisRealtimeStockLineHash,
                    this.redisStockList,
                    Long.valueOf(i * threadOnceLimit),
                    Long.valueOf(String.valueOf((i + 1) * threadOnceLimit - 1))
            );
            threadList.add(stockRealtimeLineThread);
        }
        for (StockRealtimeLineThread stockRealtimeLineThread : threadList) {
            stockRealtimeLineThread.start();
        }
        while (true) {
            if (0 >= threadList.size()) {
                break;
            }
            Iterator<StockRealtimeLineThread> iterator = threadList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isComplete()) {
                    System.out.println("remove");
                    iterator.remove();
                }
            }
            if (0 >= threadList.size()) {
                break;
            }
            try {
                //休眠
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }
}
