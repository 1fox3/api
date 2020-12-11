package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票分钟价格数据
 *
 * @author lusongsong
 * @date 2020/10/15 16:53
 */
@Data
public class StockDealMinuteEntity {
    /**
     * 记录id
     */
    Long id;
    /**
     * 股票id
     */
    Integer stockId;
    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 时间
     */
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 均价
     */
    BigDecimal avgPrice;
    /**
     * 成交量
     */
    Long dealNum;
}
