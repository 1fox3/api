package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.util.List;

/**
 * 股票日线信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Data
public class StockDayLinePo {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 线图信息
     */
    List<StockDealPo> lineNode;
}
