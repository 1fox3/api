package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealDateEntity;

/**
 * 股市交易日
 *
 * @author lusongsong
 * @date 2020/11/25 18:25
 */
@StockMapperConfig
public interface StockDealDateMapper {
    /**
     * 插入
     *
     * @param stockDealDateEntity
     * @return
     */
    Integer insert(StockDealDateEntity stockDealDateEntity);

    /**
     * 更新
     *
     * @param stockDealDateEntity
     * @return
     */
    Boolean update(StockDealDateEntity stockDealDateEntity);

    /**
     * 查询
     *
     * @param stockDealDateEntity
     * @return
     */
    StockDealDateEntity get(StockDealDateEntity stockDealDateEntity);

    /**
     * 获取上一个交易日对象
     *
     * @param stockDealDateEntity
     * @return
     */
    StockDealDateEntity pre(StockDealDateEntity stockDealDateEntity);

    /**
     * 获取当前交易日对象
     *
     * @param stockDealDateEntity
     * @return
     */
    StockDealDateEntity last(StockDealDateEntity stockDealDateEntity);

    /**
     * 获取下一个交易日对象
     *
     * @param stockDealDateEntity
     * @return
     */
    StockDealDateEntity next(StockDealDateEntity stockDealDateEntity);
}
