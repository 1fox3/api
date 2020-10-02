package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
/**
 * 股票实时交易信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class StockRealtimePo {
    /**
     * 股票id
     */
    Integer stockId;
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 股票英文名
     */
    String stockNameEn;
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
     * 价格涨幅
     */
    BigDecimal uptickPrice;
    /**
     * 增长率
     */
    BigDecimal uptickRate;
    /**
     * 波动
     */
    BigDecimal surgeRate;
    /**
     * 竞买价
     */
    BigDecimal competeBuyPrice;
    /**
     * 竞卖价
     */
    BigDecimal competeSellPrice;
    /**
     * 当前分钟最高价
     */
    BigDecimal minuteHighestPrice;
    /**
     * 当前分钟最低价
     */
    BigDecimal minuteLowestPrice;
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
    List<Map<String, BigDecimal>> buyPriceList;
    /**
     * 排名靠前的5个卖方报价
     */
    List<Map<String, BigDecimal>> sellPriceList;
    /**
     * 当前日期
     */
    String currentDate;
    /**
     * 当前时间
     */
    String currentTime;
    /**
     * 交易状态
     */
    String dealStatus;
    /**
     * 未知的数据列表
     */
    List<String> unknownKeyList;
}
