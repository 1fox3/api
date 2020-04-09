package com.fox.api.entity.dto.stock.offline;

import lombok.Data;

import java.util.List;

@Data
public class StockDealDayLineDto {
    private String stockCode;
    private String stockName;
    private List<StockDealDayDto> lineNode;
}
