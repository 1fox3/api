package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 股票按天交易信息
 */
@Data
public class StockDealDayEntity {
    //记录id
    private Integer id;
    //股票id
    private Integer stockId;
    //交易日期
    private String dt;
    //复权类型
    private Integer fqType = 0;
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
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
