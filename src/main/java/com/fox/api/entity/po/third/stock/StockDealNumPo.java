package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票成交信息
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockDealNumPo {
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 占比
     */
    BigDecimal ratio;
}
