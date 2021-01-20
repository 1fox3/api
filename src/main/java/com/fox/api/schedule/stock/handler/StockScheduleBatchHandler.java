package com.fox.api.schedule.stock.handler;

import com.fox.api.dao.stock.entity.StockEntity;

import java.util.List;

/**
 * 计划任务处理接口
 *
 * @author lusongsong
 * @date 2021/1/20 16:36
 */
public interface StockScheduleBatchHandler {
    /**
     * 计划任务批量处理股票
     *
     * @param stockEntityList
     */
    void batchHandle(List<StockEntity> stockEntityList);
}
