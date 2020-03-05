package com.fox.api.service.third.stock.entity;

import lombok.Data;

/**
 * 股票日线点信息
 */
@Data
public class StockDealEntity {
    //日期时间(2019-12-12，2019-12-12 13:00:00)
    private String dateTime;
    //开盘价
    private Double openPrice;
    //收盘价
    private Double closePrice;
    //最高价
    private Double highestPrice;
    //最低价
    private Double LowestPrice;
    //成交数量
    private Long dealNum;
    //增幅
    private Double amplitude;
}
