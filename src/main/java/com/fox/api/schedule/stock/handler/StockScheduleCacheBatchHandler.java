package com.fox.api.schedule.stock.handler;

import com.fox.spider.stock.entity.vo.StockVo;

import java.util.List;

/**
 * 计划任务处理接口
 *
 * @author lusongsong
 * @date 2021/1/20 16:38
 */
public interface StockScheduleCacheBatchHandler {

    /**
     * 计划任务批量处理股票
     *
     * @param stockVoList
     */
    void cacheBatchHandle(List<StockVo> stockVoList);
}
