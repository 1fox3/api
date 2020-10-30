package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票价格成交量信息
 *
 * @author lusongsong
 * @date 2020/10/30 15:16
 */
@Data
public class StockPriceDealNumDayEntity {
    /**
     * 记录id
     */
    Long id;
    /**
     * 股票id
     */
    Integer stockId;
    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
}
