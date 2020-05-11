package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockInfoEntity;

import java.util.List;

@StockMapperConfig
/**
 * 股票信息
 * @author lusongsong
 */
public interface StockInfoMapper {
    /**
     * 插入
     * @param stockInfoEntity
     * @return
     */
    Integer insert(StockInfoEntity stockInfoEntity);

    /**
     * 更新
     * @param stockInfoEntity
     * @return
     */
    Integer update(StockInfoEntity stockInfoEntity);

    /**
     * 查询
     * @param stockId
     * @return
     */
    StockInfoEntity getByStockId(Integer stockId);

    /**
     * 搜索
     * @param search
     * @param key
     * @return
     */
    List<StockInfoEntity> search(String search, String key);
}
