package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class StockInfoEntity {
    //记录id
    private Integer id;
    //股票id
    private Integer stockId;
    //股票所属交易所
    private Integer stockMarket = 0;
    //股票代码
    private String stockCode = "";
    //股票名称
    private String stockName = "";
    //股票英文名称
    private String stockNameEn = "";
    //股票全称
    private String stockFullName = "";
    //股票英文全称
    private String stockFullNameEn = "";
    //公司法人代表
    private String stockLegal = "";
    //股票公司注册地址
    private String stockRegisterAddress = "";
    //股票公司通讯地址
    private String stockConnectAddress = "";
    //股票公司邮箱
    private String stockEmail = "";
    //股票公司官方网址
    private String stockWebsite = "";
    //股票上市日期
    private String stockOnDate = "";
    //股票总股本(万)
    private Double stockTotalEquity = 0.0;
    //股票流通股本(万)
    private Double stockCircEquity = 0.0;
    //股票公司所在地理位置区域
    private String stockArea = "";
    //股票公司所在省份
    private String stockProvince = "";
    //股票公司所在城市
    private String stockCity = "";
    //股票所属行业
    private String stockIndustry = "";
    //股票CSRC行业(门类/大类/中类)
    private String stockCarc = "";
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
