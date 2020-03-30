package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class StockEntity {
    //记录id
    private Integer id;
    //股票代码
    private String stockCode = "";
    //股票名称
    private String stockName = "";
    //股票英文名称
    private String stockNameEn = "";
    //新浪资源的股票代码
    private String sinaStockCode = "";
    //网易资源的股票代码
    private String netsStockCode = "";
    //股票集市
    private Integer stockMarket = 0;
    //股票类型
    private Integer stockType = 0;
    //股票划分
    private Integer stockKind = 0;
    //股票状态
    private Integer stockStatus = 0;
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
