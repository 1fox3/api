package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票按月交易数据
 *
 * @author lusongsong
 * @date 2020/10/20 16:48
 */
@Data
public class StockDealMonthEntity {
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
     * 复权类型
     */
    Integer fqType = 0;
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
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 流通股本数
     */
    Long circEquity;
    /**
     * 总股本数
     */
    Long totalEquity;
}
