package com.fox.api.service.third.stock.entity;

import lombok.Data;

import java.util.List;

/**
 * 股票日线信息
 */
@Data
public class StockDayLineEntity {
    private String stockCode;
    private String stockName;
    private List<StockDealEntity> lineNode;
}
