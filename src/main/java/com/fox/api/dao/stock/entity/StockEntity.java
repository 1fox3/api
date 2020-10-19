package com.fox.api.dao.stock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 股票基本信息
 * @author lusongsong
 * @date 2020/3/5 16:13
 */
@Data
public class StockEntity {
    /**
     * 记录id
     */
    Integer id;
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
     * 股票集市
     */
    Integer stockMarket = 0;
    /**
     * 股票类型
     */
    Integer stockType = 0;
    /**
     * 股票划分
     */
    Integer stockKind = 0;
    /**
     * 股票状态
     */
    Integer stockStatus = 0;
    /**
     * 交易日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    String dealDate;
    /**
     * 交易状态
     */
    String dealStatus;
    /**
     * 未知信息
     */
    String unknownInfo;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;
}
