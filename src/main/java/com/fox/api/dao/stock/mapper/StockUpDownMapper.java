package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockUpDownEntity;

import java.util.List;

@StockMapperConfig
/**
 * 股票增幅统计
 * @author lusongsong
 */
public interface StockUpDownMapper {
    /**
     * 查询列表
     * @param orderBy
     * @param limit
     * @return
     */
    List<StockUpDownEntity> getList(String orderBy, String limit);

    /**
     * 根据股票id查询
     * @param stockId
     * @return
     */
    StockUpDownEntity getByStockId(int stockId);

    /**
     * 删除
     * @param id
     * @return
     */
    Boolean deleteById(Integer id);

    /**
     * 插入
     * @param stockUpDownEntity
     * @return
     */
    Integer insert(StockUpDownEntity stockUpDownEntity);

    /**
     * 更新
     * @param stockUpDownEntity
     * @return
     */
    Integer updateById(StockUpDownEntity stockUpDownEntity);

    /**
     * 截断表
     * @return
     */
    Boolean truncate();
}
