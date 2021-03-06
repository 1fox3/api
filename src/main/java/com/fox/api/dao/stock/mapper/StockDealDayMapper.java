package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealDayEntity;

import java.util.List;

/**
 * 股票按天交易数据
 *
 * @author lusongsong
 * @date 2020/09/24 20:15
 */
@StockMapperConfig
public interface StockDealDayMapper {
    /**
     * 插入
     *
     * @param stockDealDayEntity
     * @return
     */
    Integer insert(StockDealDayEntity stockDealDayEntity);

    /**
     * 更新
     *
     * @param stockDealDayEntity
     * @return
     */
    Boolean update(StockDealDayEntity stockDealDayEntity);

    /**
     * 批量插入数据
     *
     * @param list
     * @return
     */
    Integer batchInsert(List<StockDealDayEntity> list);

    /**
     * 创建影子表
     *
     * @return
     */
    Boolean createShadow();

    /**
     * 影子表转换
     *
     * @return
     */
    Boolean shadowConvert();

    /**
     * 删除影子表
     *
     * @return
     */
    Boolean dropShadow();

    /**
     * 获取单天记录
     *
     * @param stockDealDayEntity
     * @return
     */
    StockDealDayEntity getBySignalDate(StockDealDayEntity stockDealDayEntity);

    /**
     * 获取股票全部数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    List<StockDealDayEntity> getTotalByStock(Integer stockId, Integer fqType);

    /**
     * 获根据起止日期取按天交易数据
     *
     * @param stockId
     * @param fqType
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockDealDayEntity> getByDate(Integer stockId, Integer fqType, String startDate, String endDate);

    /**
     * 优化表
     *
     * @return
     */
    Boolean optimize();
}
