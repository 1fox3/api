package com.fox.api.entity.dto.stock.realtime;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票实时交易信息
 * @author lusongsong
 * @date 2020/4/3 14:21
 */
@Data
public class StockRealtimeInfoDto {
    /**
     * 股票id
     */
    Integer stockId;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 当前价格
     */
    BigDecimal currentPrice;
    /**
     * 开盘价
     */
    BigDecimal openPrice;
    /**
     * 最高价
     */
    BigDecimal highestPrice;
    /**
     * 最低价
     */
    BigDecimal lowestPrice;
    /**
     * 上个交易日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
}
