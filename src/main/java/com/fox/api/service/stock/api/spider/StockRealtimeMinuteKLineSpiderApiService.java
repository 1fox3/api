package com.fox.api.service.stock.api.spider;

import com.fox.api.entity.po.stock.api.StockRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.vo.StockVo;

/**
 * 股票最新交易日分钟线图数据
 *
 * @author lusongsong
 * @date 2021/1/22 16:02
 */
public interface StockRealtimeMinuteKLineSpiderApiService extends StockSpiderApiBaseInterface {
    /**
     * 股票最新交易日分钟线图数据
     *
     * @param stockVo
     * @return
     */
    StockRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo);
}
