package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票按周价格数据
 * @author lusongsong 
 * @date 2020/10/20 16:52
 */
@Data
public class StockPriceWeekEntity {
    /**
     * 记录id
    */
    Long id;
    /**
     * 股票id
    */
    Integer stockId;
    /**
     * 交易日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 复权类型，0 - 不复权， 1-复权
    */
    Integer fqType;
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
