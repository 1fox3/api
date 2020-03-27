package com.fox.api.entity.po.third.stock;

import lombok.Data;

/**
 * 分钟粒度的成交信息
 */
@Data
public class StockRealtimeNodePo {
    //分钟小时时间
    private String time;
    //价格
    private Double price;
    //均价
    private Double avgPrice;
    //成交量
    private Long dealNum;
}
