package com.fox.api.dao.stock;

import com.fox.api.annotation.mapper.StockMapperConfig;

/**
 * 股票数据表备份mapper
 *
 * @author lusongsong
 * @date 2020/12/14 13:53
 */
@StockMapperConfig
public interface StockTableBackMapper {
    /**
     * 创建备份表
     *
     * @param table
     * @return
     */
    Boolean createBakTable(String table);

    /**
     * 备份
     *
     * @param table
     * @param dt
     * @param limit
     * @return
     */
    Boolean bakData(String table, String dt, Integer limit);

    /**
     * 清楚原表数据
     *
     * @param table
     * @param dt
     * @param limit
     * @return
     */
    Integer clearOriData(String table, String dt, Integer limit);

    /**
     * 优化原表
     *
     * @param table
     * @return
     */
    Boolean optimizeOriTable(String table);
}
