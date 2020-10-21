package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockPriceMonthEntity;

import java.util.List;

/**
 * 股票按月价格数据
 * @author lusongsong
 * @date 2020/10/20 16:52
 */
@StockMapperConfig
public interface StockPriceMonthMapper {
    /**
     * 插入
     * @param stockPriceMonthEntity
     * @return
    */
    Integer insert(StockPriceMonthEntity stockPriceMonthEntity);

    /**
     * 更新
     * @param stockPriceMonthEntity
     * @return
    */
    Boolean update(StockPriceMonthEntity stockPriceMonthEntity);

    /**
     * 批量插入数据
     * @param list
     * @return
     */
    Integer batchInsert(List<StockPriceMonthEntity> list);

    /**
     * 创建影子表
     * @return
     */
    Boolean createShadow();

    /**
     * 影子表转换
     * @return
     */
    Boolean shadowConvert();

    /**
     * 删除影子表
     * @return
     */
    Boolean dropShadow();

    /**
     * 根据日期获取数据
     * @param fqType
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockPriceMonthEntity> getByDate(Integer fqType, Integer stockId, String startDate, String endDate);

    /**
     * 获取单天记录
     * @param stockPriceMonthEntity
     * @return
     */
    StockPriceMonthEntity getBySignalDate(StockPriceMonthEntity stockPriceMonthEntity);

    /**
     * 获取股票全部数据
     * @param fqType
     * @param stockId
     * @return
     */
    List<StockPriceMonthEntity> getTotalByStock(Integer fqType, Integer stockId);

    /**
     * 优化表
     * @return
     */
    Boolean optimize();
}
