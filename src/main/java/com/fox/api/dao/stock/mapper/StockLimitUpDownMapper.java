package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;

import java.util.List;

@StockMapperConfig
/**
 * 连续涨停统计
 * @author lusongsong
 */
public interface StockLimitUpDownMapper {

    /**
     * 根据类型查询列表
     * @param type
     * @param limit
     * @return
     */
    List<StockLimitUpDownEntity> getList(Integer type, String limit);

    /**
     * 根据股票id查询
     * @param stockId
     * @return
     */
    StockLimitUpDownEntity getByStockId(Integer stockId);

    /**
     * 插入
     * @param stockLimitUpDownEntity
     * @return
     */
    Integer insert(StockLimitUpDownEntity stockLimitUpDownEntity);

    /**
     * 更新
     * @param stockLimitUpDownEntity
     * @return
     */
    Integer updateById(StockLimitUpDownEntity stockLimitUpDownEntity);

    /**
     * 根据涨跌进行统计
     * @param type
     * @return
     */
    Integer countByType(Integer type);

    /**
     * 根据id进行删除
     * @param id
     * @return
     */
    Boolean deleteById(Integer id);

    /**
     * 截断表
     * @return
     */
    Boolean truncate();
}
