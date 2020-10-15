package com.fox.api.schedule.stock;

import com.fox.api.dao.stock.entity.StockEntity;

/**
 * 计划任务处理接口
 * @author lusongsong
 * @date 2020/10/15 11:17
 */
public interface StockScheduleHandler {
    /**
     * 处理单只股票
     * @param stockEntity
     */
    void handle(StockEntity stockEntity);
}
