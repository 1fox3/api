package com.fox.api.service.stock;

import com.fox.api.dao.stock.entity.StockTableDtEntity;

import java.util.List;

/**
 * 数据表数据日期管理
 *
 * @author lusongsong
 * @date 2020/10/30 17:05
 */
public interface StockTableDtService {
    /**
     * 添加
     * @param stockTableDtEntity
     * @return
     */
    Integer insert(StockTableDtEntity stockTableDtEntity);

    /**
     * 是指已被备份
     *
     * @param stockTableDtEntity
     * @return
     */
    Boolean setBak(StockTableDtEntity stockTableDtEntity);

    /**
     * 根据类型获取日期列表
     *
     * @param stockTableDtEntity
     * @return
     */
    List<String> getByType(StockTableDtEntity stockTableDtEntity);

    /**
     * 根据数据表和日期查询记录
     *
     * @param stockTableDtEntity
     * @return
     */
    StockTableDtEntity getByTableDt(StockTableDtEntity stockTableDtEntity);
}
