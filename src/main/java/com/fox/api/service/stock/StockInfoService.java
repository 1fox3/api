package com.fox.api.service.stock;

import com.fox.api.dao.stock.entity.StockInfoEntity;

public interface StockInfoService {
    StockInfoEntity getInfoFromStockExchange(Integer stockId);
    StockInfoEntity getInfo(Integer stockId);
}
