package com.fox.api.service.stock.dealinfo;

import com.fox.api.entity.po.stock.dealinfo.StockRealtimeMinuteDealInfoPo;
import com.fox.spider.stock.entity.vo.StockVo;

/**
 * 股票分钟交易信息
 *
 * @author lusongsong
 * @date 2020/12/11 11:41
 */
public interface StockMinuteDealInfoService {
    /**
     * 从爬虫数据接口中获取实时分钟交易信息
     *
     * @param stockVo
     * @return
     */
    StockRealtimeMinuteDealInfoPo realtimeFromSpiderApi(StockVo stockVo);
}
