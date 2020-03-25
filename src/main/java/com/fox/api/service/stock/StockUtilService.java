package com.fox.api.service.stock;

import com.fox.api.entity.property.stock.StockKindInfoProperty;

public interface StockUtilService {
    StockKindInfoProperty getStockKindInfo(String stockCode, Integer stockMarket);
}
