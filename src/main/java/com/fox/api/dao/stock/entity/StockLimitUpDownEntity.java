package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 连续涨跌停股票
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockLimitUpDownEntity {
    /**
     * 记录id
     */
    Integer id;
    /**
     * 股票id
     */
    Integer stockId;
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String startDate;
    /**
     * 当前日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String currentDate;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;
}
