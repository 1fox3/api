package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 股市交易日
 *
 * @author lusongsong 
 * @date 2020/11/25 18:25
 */
@Data
public class StockDealDateEntity {
    /**
     * 记录id
    */
    Integer id;
    /**
     * 日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dt;
    /**
     * 股市
    */
    Integer stockMarket;
    /**
     * 是否为交易日
     */
    Integer type;
    /**
     * 是否已锁定，不会自动修改
    */
    Integer isLocked;
}
