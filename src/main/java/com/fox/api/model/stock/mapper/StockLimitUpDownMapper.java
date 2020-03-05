package com.fox.api.model.stock.mapper;

import com.fox.api.common.config.mapper.StockMapperConfig;
import com.fox.api.model.stock.entity.StockLimitUpDownEntity;

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
