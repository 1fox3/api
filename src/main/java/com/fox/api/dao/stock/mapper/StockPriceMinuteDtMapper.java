package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockPriceMinuteDtEntity;

import java.util.List;

/**
 * 分钟日期管理
 * @author lusongsong
 * @date 2020/10/16 16:23
 */
@StockMapperConfig
public interface StockPriceMinuteDtMapper {
    /**
     * 插入
     * @param stockPriceMinuteDtEntity
     * @return
    */
    Integer insert(StockPriceMinuteDtEntity stockPriceMinuteDtEntity);

    /**
     * 更新
     * @param stockPriceMinuteDtEntity
     * @return
    */
    Boolean update(StockPriceMinuteDtEntity stockPriceMinuteDtEntity);

    /**
     * 根据类型获取
     * @param type
     * @return
     */
    List<StockPriceMinuteDtEntity> getByType(Integer type);
}
