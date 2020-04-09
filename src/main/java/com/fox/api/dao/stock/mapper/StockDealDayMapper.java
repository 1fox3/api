package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealDayEntity;

import java.util.List;

@StockMapperConfig
public interface StockDealDayMapper {
    Integer insert(StockDealDayEntity stockDealDayEntity);
    Integer batchInsert(List<StockDealDayEntity> list);
    Boolean truncate();
    Boolean optimize();
    List<StockDealDayEntity> getByDate(Integer stockId, String startDate, String endDate, Integer fqType);
}
