package com.fox.api.entity.po.third.stock;

import lombok.Data;

import java.util.List;

/**
 * 股票日线信息
 */
@Data
public class StockDayLinePo {
    private String stockCode;
    private String stockName;
    private List<StockDealPo> lineNode;
}
