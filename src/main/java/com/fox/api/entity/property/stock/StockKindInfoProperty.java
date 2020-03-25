package com.fox.api.entity.property.stock;

import lombok.Data;

import java.util.List;

@Data
public class StockKindInfoProperty {
    private String stockKindName;
    private Integer stockMarket;
    private Integer stockType;
    private Integer stockKind;
    private List<String> perCode;
}
