package com.fox.api.entity.dto.stock.realtime.rank;

import lombok.Data;

@Data
public class StockRealtimeRankInfoDto {
    //股票id
    private Integer stockId;
    //股票代码
    private String stockCode;
    //股票名称
    private String stockName;
    //涨幅
    private Double uptickRate;
    //波动
    private Double surgeRate;
    //成交量
    private Double dealNum;
    //成交金额
    private Double dealMoney;
}
