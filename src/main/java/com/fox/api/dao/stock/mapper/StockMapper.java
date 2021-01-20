package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 股票信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@StockMapperConfig
@CacheConfig(cacheNames = {"StockMapper"})
public interface StockMapper {
    /**
     * 插入
     * @param stockEntity
     * @return
     */
    Integer insert(StockEntity stockEntity);

    /**
     * 更新
     * @param stockEntity
     * @return
     */
    Integer update(StockEntity stockEntity);

    /**
     * 根据id查询单条记录
     * @param id
     * @return
     */
    @Cacheable(key = "#id", unless="#result == null", cacheManager = "stockCacheManager")
    StockEntity getById(int id);

    /**
     * 根据股票编码获取单条记录
     * @param stockCode
     * @param stockMarket
     * @return
     */
    StockEntity getByStockCode(String stockCode, int stockMarket);

    /**
     * 获取列表
     * @param stockType
     * @param id
     * @param stockMarket
     * @param stockStatus
     * @param limit
     * @return
     */
    List<StockEntity> getListByType(Integer stockType, Integer id, Integer stockMarket, Integer stockStatus, String limit);

    /**
     * 优化表
     * @return
     */
    Boolean optimize();

    /**
     * 根据id获取列表
     * @param id
     * @param limit
     * @return
     */
    List<StockEntity> getListById(Integer id, Integer limit);
}
