package com.fox.api.entity.dto.stock.updown;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票涨跌统计信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockUpDownDto {
    /**
     * 股票id
     */
    int stockId;
    /**
     * 股票代码
     */
    String stockCode = "";
    /**
     * 股票名称
     */
    String stockName = "";
    /**
     * 股票英文名称
     */
    String stockNameEn = "";
    /**
     * 近10个交易日涨幅
     */
    BigDecimal d10Up;
    /**
     * 近10个交易日降幅
     */
    BigDecimal d10Down;
    /**
     * 近30个交易日涨幅
     */
    BigDecimal d30Up;
    /**
     * 近30个交易日降幅
     */
    BigDecimal d30Down;
    /**
     * 近50个交易日涨幅
     */
    BigDecimal d50Up;
    /**
     * 近50个交易日降幅
     */
    BigDecimal d50Down;
    /**
     * 近100个交易日涨幅
     */
    BigDecimal d100Up;
    /**
     * 近100个交易日降幅
     */
    BigDecimal d100Down;
    /**
     * 近200个交易日涨幅
     */
    BigDecimal d200Up;
    /**
     * 近200个交易日降幅
     */
    BigDecimal d200Down;
    /**
     * 近300个交易日涨幅
     */
    BigDecimal d300Up;
    /**
     * 近300个交易日降幅
     */
    BigDecimal d300Down;
}
