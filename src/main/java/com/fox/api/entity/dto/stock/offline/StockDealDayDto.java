package com.fox.api.entity.dto.stock.offline;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票按天交易信息
 * @author lusongsong
 * @date 2020/4/9 16:57
 */
@Data
public class StockDealDayDto {
    /**
     * 交易日期
     */
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
     * 成交数量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
    /**
     * 增幅
     */
    BigDecimal uptickRate;
}
