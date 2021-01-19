package com.fox.api.entity.po.stock.api;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * 股票实时交易信息
 *
 * @author lusongsong
 * @date 2021/1/15 15:06
 */
@Data
public class StockRealtimeDealInfoPo implements Serializable {
    /**
     * 股票交易所
     */
    Integer stockMarket;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 当前日期
     */
    String dt;
    /**
     * 当前时间
     */
    String time;
    /**
     * 当前价格
     */
    BigDecimal currentPrice;
    /**
     * 今日开盘价
     */
    BigDecimal openPrice;
    /**
     * 今日最高价
     */
    BigDecimal highestPrice;
    /**
     * 今日最低价
     */
    BigDecimal lowestPrice;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 排名靠前的5个买方报价
     */
    LinkedHashMap<BigDecimal, Long> buyPriceMap;
    /**
     * 排名靠前的5个卖方报价
     */
    LinkedHashMap<BigDecimal, Long> sellPriceMap;
}
