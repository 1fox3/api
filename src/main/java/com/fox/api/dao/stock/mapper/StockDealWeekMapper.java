package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealWeekEntity;

import java.util.List;

/**
 * 股票按周交易数据
 * @author lusongsong
 * @date 2020/10/20 16:48
 */
@StockMapperConfig
public interface StockDealWeekMapper {
    /**
     * 插入
     * @param stockDealWeekEntity
     * @return
    */
    Integer insert(StockDealWeekEntity stockDealWeekEntity);

    /**
     * 更新
     * @param stockDealWeekEntity
     * @return
    */
    Boolean update(StockDealWeekEntity stockDealWeekEntity);
    
    /**
     * 批量插入数据
     * @param list
     * @return
     */
    Integer batchInsert(List<StockDealWeekEntity> list);

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
     * 获取单天记录
     * @param stockDealWeekEntity
     * @return
     */
    StockDealWeekEntity getBySignalDate(StockDealWeekEntity stockDealWeekEntity);

    /**
     * 获取股票全部数据
     * @param stockId
     * @return
     */
    List<StockDealWeekEntity> getTotalByStock(Integer stockId);

    /**
     * 获根据起止日期取按天交易数据
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockDealWeekEntity> getByDate(Integer stockId, String startDate, String endDate);

    /**
     * 优化表
     * @return
     */
    Boolean optimize();
}
