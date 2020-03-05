package com.fox.api.model.stock.mapper;

import com.fox.api.common.config.mapper.StockMapperConfig;
import com.fox.api.model.stock.entity.StockUpDownEntity;

import java.util.List;

@StockMapperConfig
public interface StockUpDownMapper {
    List<StockUpDownEntity> getList(String orderBy, String limit);
    StockUpDownEntity getByStockId(int stockId);
    Boolean deleteById(Integer id);
    Integer insert(StockUpDownEntity stockUpDownEntity);
    Integer updateById(StockUpDownEntity stockUpDownEntity);
}
