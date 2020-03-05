package com.fox.api.service.stock.entity.updown;

import lombok.Data;

@Data
public class StockUpDown {
    //股票id
    private int stockId;
    //近10个交易日涨幅
    private float d10Up;
    //近10个交易日降幅
    private float d10Down;
    //近30个交易日涨幅
    private float d30Up;
    //近30个交易日降幅
    private float d30Down;
    //近50个交易日涨幅
    private float d50Up;
    //近50个交易日降幅
    private float d50Down;
    //近100个交易日涨幅
    private float d100Up;
    //近100个交易日降幅
    private float d100Down;
    //近200个交易日涨幅
    private float d200Up;
    //近200个交易日降幅
    private float d200Down;
    //近300个交易日涨幅
    private float d300Up;
    //近300个交易日降幅
    private float d300Down;
    //股票代码
    private String stockCode = "";
    //股票名称
    private String stockName = "";
    //股票英文名称
    private String stockNameEn = "";
}
