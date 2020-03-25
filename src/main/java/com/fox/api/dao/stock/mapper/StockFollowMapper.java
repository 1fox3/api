package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockFollowEntity;

import java.util.List;

@StockMapperConfig
public interface StockFollowMapper {
    List<StockFollowEntity> getByUser(int userId);
}
