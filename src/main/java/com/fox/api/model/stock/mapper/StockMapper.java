package com.fox.api.model.stock.mapper;

import com.fox.api.common.config.mapper.StockMapperConfig;
import com.fox.api.model.stock.entity.StockEntity;

@StockMapperConfig
public interface StockMapper {
    Integer insert(StockEntity stockEntity);

    Integer update(StockEntity stockEntity);

    StockEntity getById(int id);

    StockEntity getByStockCode(String stockCode, int stockMarket);

    /**
     * 获取最后的id值
     * @return
     */
    int getLastId();
}
