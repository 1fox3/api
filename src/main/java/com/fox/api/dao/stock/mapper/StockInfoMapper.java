package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockInfoEntity;

@StockMapperConfig
public interface StockInfoMapper {
    Integer insert(StockInfoEntity stockInfoEntity);
    Integer update(StockInfoEntity stockInfoEntity);
    StockInfoEntity getByStockId(Integer stockId);
}
