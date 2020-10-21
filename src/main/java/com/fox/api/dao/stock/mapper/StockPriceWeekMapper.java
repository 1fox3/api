package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockPriceWeekEntity;

import java.util.List;

/**
 * 股票按周价格数据
 * @author lusongsong
 * @date 2020/10/20 16:52
 */
@StockMapperConfig
public interface StockPriceWeekMapper {
    /**
     * 插入
     * @param stockPriceWeekEntity
     * @return
    */
    Integer insert(StockPriceWeekEntity stockPriceWeekEntity);

    /**
     * 更新
     * @param stockPriceWeekEntity
     * @return
    */
    Boolean update(StockPriceWeekEntity stockPriceWeekEntity);

    /**
     * 批量插入数据
     * @param list
     * @return
     */
    Integer batchInsert(List<StockPriceWeekEntity> list);

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
    List<StockPriceWeekEntity> getByDate(Integer fqType, Integer stockId, String startDate, String endDate);

    /**
     * 获取单天记录
     * @param stockPriceWeekEntity
     * @return
     */
    StockPriceWeekEntity getBySignalDate(StockPriceWeekEntity stockPriceWeekEntity);

    /**
     * 获取股票全部数据
     * @param fqType
     * @param stockId
     * @return
     */
    List<StockPriceWeekEntity> getTotalByStock(Integer fqType, Integer stockId);

    /**
     * 优化表
     * @return
     */
    Boolean optimize();
}
