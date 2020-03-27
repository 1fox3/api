package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.util.List;

/**
 * 当天成交信息线图
 */
@Data
public class StockRealtimeLinePo {
    //股票编号
    private String stockCode;
    //股票名称
    private String stockName;
    //日期
    private String date;
    //线图的点数量
    private Integer nodeCount;
    //昨日收盘价
    private Double yesterdayClosePrice;
    //成交股数
    private Long dealNum;
    //分钟粒度的成交信息
    private List<StockRealtimeNodePo> lineNode;
}
