package com.fox.api.schedule.stock.handler;

import java.util.List;

/**
 * 计划任务处理接口
 *
 * @author lusongsong
 * @date 2021/1/20 16:37
 */
public interface StockScheduleCacheBatchCodeHandler {
    /**
     * 计划任务批量处理股票
     *
     * @param stockCodeList
     */
    void cacheBatchCodeHandle(List<String> stockCodeList);
}
