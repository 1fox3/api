package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealMinuteEntity;

import java.util.List;

/**
 * 股票分钟交易信息数据
 *
 * @author lusongsong
 * @date 2020/10/15 16:53
 */
@StockMapperConfig
public interface StockDealMinuteMapper {
    /**
     * 插入
     *
     * @param stockDealMinuteEntity
     * @return
     */
    Integer insert(StockDealMinuteEntity stockDealMinuteEntity);

    /**
     * 批量插入数据
     *
     * @param list
     * @return
     */
    Integer batchInsert(List<StockDealMinuteEntity> list);

    /**
     * 优化表
     *
     * @return
     */
    Boolean optimize();

    /**
     * 获取固定数量的点
     *
     * @param stockId
     * @param len
     * @return
     */
    List<StockDealMinuteEntity> len(Integer stockId, Integer len);
}
