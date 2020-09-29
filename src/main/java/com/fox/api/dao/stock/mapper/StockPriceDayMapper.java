package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockPriceDayEntity;

import java.util.List;

/**
 * 股票按天价格数据
 * @author lusongsong
 * @date 2020/09/24 20:09
 */
@StockMapperConfig
public interface StockPriceDayMapper {
    /**
     * 插入
     * @param stockPriceDayEntity
     * @return
    */
    Integer insert(StockPriceDayEntity stockPriceDayEntity);

    /**
     * 更新
     * @param stockPriceDayEntity
     * @return
    */
    Boolean update(StockPriceDayEntity stockPriceDayEntity);

    /**
     * 批量插入数据
     * @param list
     * @return
     */
    Integer batchInsert(List<StockPriceDayEntity> list);

    /**
     * 创建影子表
     * @return
     */
    Boolean createShadowTable();

    /**
     * 影子表转换
     * @return
     */
    Boolean shadowTableConvert();

    /**
     * 删除影子表
     * @return
     */
    Boolean dropShadow();

    /**
     * 根据日期获取数据
     * @param faType
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockPriceDayEntity> getByDate(Integer faType, Integer stockId, String startDate, String endDate);

    /**
     * 获取单天记录
     * @param stockPriceDayEntity
     * @return
     */
    StockPriceDayEntity getBySignalDate(StockPriceDayEntity stockPriceDayEntity);
}
