package com.fox.api.dao.stock.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票增幅统计
 *
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
     * 天数
     */
    Integer dayNum;
    /**
     * 增幅
     */
    BigDecimal upRate;
    /**
     * 跌幅
     */
    BigDecimal downRate;
}
