package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
/**
 * 股票实时交易信息
 * @author lusongsong
 */
public class StockRealtimePo {
    /**
     * 股票名称
     */
    private String stockName;
    /**
     * 股票英文名
     */
    private String stockNameEn;
    /**
     * 今日开盘价
     */
    private Float todayOpenPrice;
    /**
     * 昨日收盘价
     */
    private Float yesterdayClosePrice;
    /**
     * 当前价格
     */
    private Float currentPrice;
    /**
     * 今日最高价
     */
    private Float todayHighestPrice;
    /**
     * 今日最低价
     */
    private Float todayLowestPrice;
    /**
     * 价格涨幅
     */
    private Float uptickPrice;
    /**
     * 增长率
     */
    private Float uptickRate;
    /**
     * 竞买价
     */
    private Float competeBuyPrice;
    /**
     * 竞卖价
     */
    private Float competeSellPrice;
    /**
     * 当前分钟最高价
     */
    private Float minuteHighestPrice;
    /**
     * 当前分钟最低价
     */
    private Float minuteLowestPrice;
    /**
     * 成交股数
     */
    private Long dealNum;
    /**
     * 成交金额
     */
    private Double dealMoney;
    /**
     * 排名靠前的5个买方报价
     */
    private List<Map<String, Float>> buyPriceList;
    /**
     * 排名靠前的5个卖方报价
     */
    private List<Map<String, Float>> sellPriceList;
    /**
     * 当前日期
     */
    private String currentDate;
    /**
     * 当前时间
     */
    private String currentTime;
    /**
     * 交易状态
     */
    private String dealStatus;
    /**
     * 未知的数据列表
     */
    private List<String> unknownKeyList;
}
