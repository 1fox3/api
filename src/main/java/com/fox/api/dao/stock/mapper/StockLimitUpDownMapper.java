package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;

import java.util.List;

@StockMapperConfig
public interface StockLimitUpDownMapper {
    List<StockLimitUpDownEntity> getList(Integer type, String limit);
    StockLimitUpDownEntity getByStockId(Integer stockId);
    Integer insert(StockLimitUpDownEntity stockLimitUpDownEntity);
    Integer updateById(StockLimitUpDownEntity stockLimitUpDownEntity);
    Integer countByType(Integer type);
    Boolean deleteById(Integer id);
}
