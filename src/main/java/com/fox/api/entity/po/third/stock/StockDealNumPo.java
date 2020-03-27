package com.fox.api.entity.po.third.stock;

import lombok.Data;

/**
 * 股票成交信息
 */
@Data
public class StockDealNumPo {
    //价格
    private Float price;
    //成交量
    private Long dealNum;
    //占比
    private Float ratio;
}
