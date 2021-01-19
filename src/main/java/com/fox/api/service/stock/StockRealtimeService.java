package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.realtime.StockRealtimeInfoDto;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.sina.SinaRealtimeDealInfoPo;

import java.util.List;
import java.util.Map;

/**
 * 股票实时交易数据相关
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public interface StockRealtimeService {

    /**
     * 获取股票实时交易信息
     *
     * @param stockId
     * @return
     */
    SinaRealtimeDealInfoPo info(Integer stockId);

    /**
     * 分钟线图
     *
     * @param stockId
     * @return
     */
    NetsRealtimeMinuteKLinePo line(Integer stockId);

    List<StockRealtimeInfoDto> topIndex();

    /**
     * 股市股票涨跌数量统计
     *
     * @param stockMarket
     * @return
     */
    Map<String, Integer> uptickRateStatistics(Integer stockMarket);
}
