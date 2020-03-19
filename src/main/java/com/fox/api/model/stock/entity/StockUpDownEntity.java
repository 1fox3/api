package com.fox.api.model.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class StockUpDownEntity {
    //记录id
    private Integer id;
    //股票id
    private Integer stockId;
    //近10个交易日涨幅
    private Float d10Up;
    //近10个交易日降幅
    private Float d10Down;
    //近30个交易日涨幅
    private Float d30Up;
    //近30个交易日降幅
    private Float d30Down;
    //近50个交易日涨幅
    private Float d50Up;
    //近50个交易日降幅
    private Float d50Down;
    //近100个交易日涨幅
    private Float d100Up;
    //近100个交易日降幅
    private Float d100Down;
    //近200个交易日涨幅
    private Float d200Up;
    //近200个交易日降幅
    private Float d200Down;
    //近300个交易日涨幅
    private Float d300Up;
    //近300个交易日降幅
    private Float d300Down;
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
