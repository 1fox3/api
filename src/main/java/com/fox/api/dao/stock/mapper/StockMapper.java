package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockEntity;

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
