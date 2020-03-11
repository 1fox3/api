package com.fox.api.service.stock;

import com.fox.api.config.stock.entity.StockKindInfoEntity;

import java.util.Map;

public interface StockUtilService {
    StockKindInfoEntity getStockKindInfo(String stockCode, Integer stockMarket);
}
