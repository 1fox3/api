package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockPriceMinuteEntity;

import java.util.List;

/**
 * 股票分钟价格数据
 * @author lusongsong
 * @date 2020/10/15 16:53
 */
@StockMapperConfig
public interface StockPriceMinuteMapper {
    /**
     * 插入
     * @param stockPriceMinuteEntity
     * @return
    */
    Integer insert(StockPriceMinuteEntity stockPriceMinuteEntity);

    /**
     * 批量插入数据
     * @param list
     * @return
     */
    Integer batchInsert(List<StockPriceMinuteEntity> list);

    /**
     * 查询所有日期
     * @return
     */
    List<String> dtList();

    /**
     * 查询某天记录条数
     * @param dt
     * @return
     */
    Integer dtRowCount(String dt);

    /**
     * 备份
     * @param dt
     * @param limit
     * @return
     */
    Boolean bak(String dt, Integer limit);

    /**
     * 删除
     * @param dt
     * @param limit
     * @return
     */
    Integer delete(String dt, Integer limit);

    /**
     * 优化表
     * @return
     */
    Boolean optimize();
}
