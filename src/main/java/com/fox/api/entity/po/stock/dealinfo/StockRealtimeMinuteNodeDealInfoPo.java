package com.fox.api.entity.po.stock.dealinfo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实时交易分钟信息
 *
 * @author lusongsong
 * @date 2020/12/11 13:45
 */
@Data
public class StockRealtimeMinuteNodeDealInfoPo {
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
