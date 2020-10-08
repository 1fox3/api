package com.fox.api.entity.property.stock;

import lombok.Data;

/**
 * 股票代码信息
 * @author lusongsong
 * @date 2020/10/8 10:50
 */
@Data
public class StockCodeProperty {
    /**
     * 股票集市
     */
    Integer stockMarket;
    /**
     * 股票代码
     */
    String stockCode;
}
