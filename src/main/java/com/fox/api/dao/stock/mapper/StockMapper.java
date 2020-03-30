package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@StockMapperConfig
@CacheConfig(cacheNames = {"StockMapper"})
public interface StockMapper {
    Integer insert(StockEntity stockEntity);

    Integer update(StockEntity stockEntity);

    @Cacheable(key = "#id", cacheManager = "stockCacheManager")
    StockEntity getById(int id);

    StockEntity getByStockCode(String stockCode, int stockMarket);

    List<StockEntity> getListByType(Integer stockType, Integer id, String limit, List<Integer> stockMarket);

    /**
     * 获取最后的id值
     * @return
     */
    int getLastId();
}
