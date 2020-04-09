package com.fox.api.entity.dto.stock.offline;

import lombok.Data;

@Data
public class StockDealDayDto {
    //交易日期
    private String dt;
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
    //成交金额
    private Double dealMoney = 0.0;
    //增幅
    private Double uptickRate;
}
