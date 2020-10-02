package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 当天成交信息线图
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockRealtimeLinePo {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 日期
     */
    String dt;
    /**
     * 线图的点数量
     */
    Integer nodeCount;
    /**
     * 昨日收盘价
     */
    BigDecimal preClosePrice;
    /**
     * 成交股数
     */
    Long dealNum;
    /**
     * 分钟粒度的成交信息
     */
    List<StockRealtimeNodePo> lineNode;
}
