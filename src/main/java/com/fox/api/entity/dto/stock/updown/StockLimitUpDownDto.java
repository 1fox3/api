package com.fox.api.entity.dto.stock.updown;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 股票连续涨跌停信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockLimitUpDownDto {
    /**
     * 股票id
     */
    Integer stockId;
    /**
     * 股票代码
     */
    String stockCode = "";
    /**
     * 股票名称
     */
    String stockName = "";
    /**
     * 股票英文名称
     */
    String stockNameEn = "";
    /**
     * 涨跌类型
     */
    Integer type;
    /**
     * 连续次数
     */
    Integer num;
    /**
     * 开始价格
     */
    Float startPrice;
    /**
     * 当前价格
     */
    Float currentPrice;
    /**
     * 开始日期
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    String startDate;
    /**
     * 当前日期
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    String currentDate;
}
