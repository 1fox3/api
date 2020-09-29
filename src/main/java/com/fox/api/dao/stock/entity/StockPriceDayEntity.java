package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票按天价格数据
 * @author lusongsong 
 * @date 2020/09/24 20:09
 */
@Data
public class StockPriceDayEntity {
    /**
     * 记录id
    */
    Integer id;
    /**
     * 股票id
    */
    Integer stockId;
    /**
     * 复权类型
     */
    Integer fqType = 0;
    /**
     * 交易日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 开盘价
    */
    BigDecimal openPrice;
    /**
     * 收盘价
    */
    BigDecimal closePrice;
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
}
