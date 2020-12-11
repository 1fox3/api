package com.fox.api.entity.po.stock.dealinfo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 股票实时分钟交易信息
 *
 * @author lusongsong
 * @date 2020/12/11 11:46
 */
@Data
public class StockRealtimeMinuteDealInfoPo {
    /**
     * 股票所属交易所
     */
    Integer stockMarket;
    /**
     * 股票编码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 日期
     */
    String dt;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 分钟粒度的成交信息
     */
    List<StockRealtimeMinuteNodeDealInfoPo> klineData;
}
