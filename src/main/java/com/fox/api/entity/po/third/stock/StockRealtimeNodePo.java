package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分钟粒度的成交信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockRealtimeNodePo {
    /**
     * 分钟小时时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 均价
     */
    BigDecimal avgPrice;
    /**
     * 成交量
     */
    Long dealNum;
}
