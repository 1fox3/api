package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockUpDownEntity;

import java.util.List;

@StockMapperConfig
public interface StockUpDownMapper {
    List<StockUpDownEntity> getList(String orderBy, String limit);
    StockUpDownEntity getByStockId(int stockId);
    Boolean deleteById(Integer id);
    Integer insert(StockUpDownEntity stockUpDownEntity);
    Integer updateById(StockUpDownEntity stockUpDownEntity);
}
