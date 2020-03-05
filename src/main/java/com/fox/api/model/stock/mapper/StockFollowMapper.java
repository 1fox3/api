package com.fox.api.model.stock.mapper;

import com.fox.api.common.config.mapper.StockMapperConfig;
import com.fox.api.model.stock.entity.StockFollowEntity;

import java.util.List;

@StockMapperConfig
public interface StockFollowMapper {
    List<StockFollowEntity> getByUser(int userId);
}
