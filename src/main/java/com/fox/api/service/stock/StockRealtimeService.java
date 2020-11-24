package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.realtime.StockRealtimeInfoDto;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;

import java.util.List;
import java.util.Map;

/**
 * 股票实时交易数据相关
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public interface StockRealtimeService {

    StockRealtimePo info(Integer stockId);

    StockRealtimeLinePo line(Integer stockId);

    List<StockRealtimeInfoDto> topIndex();

    /**
     * 股市股票涨跌数量统计
     *
     * @param stockMarket
     * @return
     */
    Map<String, Integer> uptickRateStatistics(Integer stockMarket);
}
