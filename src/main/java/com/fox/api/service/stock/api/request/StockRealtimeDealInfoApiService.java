package com.fox.api.service.stock.api.request;

import com.fox.api.entity.po.stock.api.StockRealtimeDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;

import java.util.List;
import java.util.Map;

/**
 * 股票最新交易日交易信息接口
 *
 * @author lusongsong
 * @date 2021/1/15 17:36
 */
public interface StockRealtimeDealInfoApiService extends StockBaseApiService {
    /**
     * 获取单只股票的实时交易信息
     *
     * @param stockVo
     * @return
     */
    StockRealtimeDealInfoPo realtimeDealInfo(StockVo stockVo);

    /**
     * 批量获取股票的实时交易信息
     *
     * @param stockVoList
     * @return
     */
    Map<String, StockRealtimeDealInfoPo> batchRealtimeDealInfo(List<StockVo> stockVoList);
}
