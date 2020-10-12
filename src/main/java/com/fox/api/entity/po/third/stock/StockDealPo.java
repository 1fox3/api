package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票日线点信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockDealPo {
    /**
     * 日期时间(2019-12-12，2019-12-12 13:00:00)
     */
    String dateTime;
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
     * 成交数量
     */
    Long dealNum;
    /**
     * 增幅
     */
    BigDecimal uptickRate;
}
