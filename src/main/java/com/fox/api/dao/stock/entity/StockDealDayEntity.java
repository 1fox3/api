package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票按天交易数据
 * @author lusongsong 
 * @date 2020/09/24 20:15
 */
@Data
public class StockDealDayEntity {
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
     * 收盘价
    */
    BigDecimal closePrice;
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
