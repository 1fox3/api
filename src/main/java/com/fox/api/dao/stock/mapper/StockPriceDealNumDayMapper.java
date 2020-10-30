package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockPriceDealNumDayEntity;

import java.util.List;

/**
 * 股票价格成交量信息
 *
 * @author lusongsong
 * @date 2020/10/30 15:16
 */
@StockMapperConfig
public interface StockPriceDealNumDayMapper {
    /**
     * 插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(List<StockPriceDealNumDayEntity> list);

    /**
     * 根据日期获取数据
     *
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockPriceDealNumDayEntity> getByDate(Integer stockId, String startDate, String endDate);

    /**
     * 根据日期删除股票价格成交量信息
     *
     * @param stockId
     * @param dt
     * @return
     */
    Integer deleteByDate(Integer stockId, String dt);

    /**
     * 创建备份表
     *
     * @return
     */
    Boolean createBak();

    /**
     * 根据日期备份
     *
     * @param dt
     * @param limit
     * @return
     */
    Integer bakByDate(String dt, Integer limit);

    /**
     * 根据日期清除数据
     *
     * @param dt
     * @param limit
     * @return
     */
    Integer clearByDate(String dt, Integer limit);

    /**
     * 优化表
     *
     * @return
     */
    Boolean optimize();
}
