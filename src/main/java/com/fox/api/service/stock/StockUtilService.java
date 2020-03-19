package com.fox.api.service.stock;

import com.fox.api.config.stock.entity.StockKindInfoEntity;

public interface StockUtilService {
    StockKindInfoEntity getStockKindInfo(String stockCode, Integer stockMarket);
}
