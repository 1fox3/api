package com.fox.api.config.stock.entity;

import lombok.Data;

import java.util.List;

@Data
public class StockKindInfoEntity {
    private String stockKindName;
    private Integer stockMarket;
    private Integer stockType;
    private Integer stockKind;
    private List<String> perCode;
}
