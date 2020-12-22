package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockUpDownEntity;

import java.util.List;

/**
 * 股票增幅统计
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@StockMapperConfig
public interface StockUpDownMapper {
    /**
     * 查询列表
     *
     * @param orderBy
     * @param limit
     * @return
     */
    List<StockUpDownEntity> getList(String orderBy, String limit);

    /**
     * 插入
     *
     * @param stockUpDownEntity
     * @return
     */
    Integer insert(StockUpDownEntity stockUpDownEntity);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(List<StockUpDownEntity> list);

    /**
     * 创建影子表
     *
     * @return
     */
    Boolean createShadow();

    /**
     * 影子表转换
     *
     * @return
     */
    Boolean shadowConvert();

    /**
     * 删除影子表
     *
     * @return
     */
    Boolean dropShadow();

    /**
     * 优化表
     *
     * @return
     */
    Boolean optimize();
}
