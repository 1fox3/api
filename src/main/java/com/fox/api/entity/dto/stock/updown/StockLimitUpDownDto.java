package com.fox.api.entity.dto.stock.updown;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class StockLimitUpDownDto {
    //股票id
    private Integer stockId;
    //股票代码
    private String stockCode = "";
    //股票名称
    private String stockName = "";
    //股票英文名称
    private String stockNameEn = "";
    //涨跌类型
    private Integer type;
    //连续次数
    private Integer num;
    //开始价格
    private Float startPrice;
    //当前价格
    private Float currentPrice;
    //开始日期
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private String startDate;
    //当前日期
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private String currentDate;
}
