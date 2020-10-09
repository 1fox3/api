package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockLimitUpDownEntity;

import java.util.List;

@StockMapperConfig
/**
 * 连续涨停统计
 * @author lusongsong
 * @date 2020/3/5 18:13
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
     * 插入
     * @param stockLimitUpDownEntity
     * @return
     */
    Integer insert(StockLimitUpDownEntity stockLimitUpDownEntity);

    /**
     * 根据涨跌进行统计
     * @param type
     * @return
     */
    Integer countByType(Integer type);

    /**
     * 截断表
     * @return
     */
    Boolean truncate();

    /**
     * 优化表
     * @return
     */
    Boolean optimize();
}
