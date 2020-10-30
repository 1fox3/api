package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockPriceMinuteDtEntity;
import com.fox.api.dao.stock.entity.StockTableDtEntity;

import java.util.List;

/**
 * 数据表数据日期管理
 *
 * @author lusongsong
 * @date 2020/10/30 16:52
 */
@StockMapperConfig
public interface StockTableDtMapper {
    /**
     * 插入
     *
     * @param stockTableDtEntity
     * @return
    */
    Integer insert(StockTableDtEntity stockTableDtEntity);

    /**
     * 更新
     *
     * @param stockTableDtEntity
     * @return
    */
    Boolean update(StockTableDtEntity stockTableDtEntity);

    /**
     * 获取日期列表
     *
     * @param stockTableDtEntity
     * @return
     */
    List<String> getDtByType(StockTableDtEntity stockTableDtEntity);
}
