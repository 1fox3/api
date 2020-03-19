package com.fox.api.model.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class StockLimitUpDownEntity {
    //记录id
    private Integer id;
    //股票id
    private Integer stockId;
    //涨跌类型
    private Integer type;
    //连续次数
    private Integer num;
    //开始价格
    private Float startPrice;
    //当前价格
    private Float currentPrice;
    //开始日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String startDate;
    //当前日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String currentDate;
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;
}
