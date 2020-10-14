package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票增幅统计
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockUpDownEntity {
    /**
     * 记录id
     */
    Integer id;
    /**
     * 股票id
     */
    Integer stockId;
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
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;
}
