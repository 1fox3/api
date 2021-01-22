package com.fox.api.service.stock.api.request;

import com.fox.api.entity.po.stock.api.StockRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.vo.StockVo;

/**
 * 股票最新交易日分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/22 16:40
 */
public interface StockRealtimeMinuteKLineApiService extends StockBaseApiService {
    /**
     * 股票最新交易日分钟线图数据
     *
     * @param stockVo
     * @return
     */
    StockRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo);
}
