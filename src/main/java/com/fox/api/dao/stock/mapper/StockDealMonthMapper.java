package com.fox.api.dao.stock.mapper;

import com.fox.api.annotation.mapper.StockMapperConfig;
import com.fox.api.dao.stock.entity.StockDealMonthEntity;

import java.util.List;

/**
 * 股票按天交易数据
 *
 * @author lusongsong
 * @date 2020/09/24 20:15
 */
@StockMapperConfig
public interface StockDealMonthMapper {
    /**
     * 插入
     *
     * @param stockDealMonthEntity
     * @return
     */
    Integer insert(StockDealMonthEntity stockDealMonthEntity);

    /**
     * 更新
     *
     * @param stockDealMonthEntity
     * @return
     */
    Boolean update(StockDealMonthEntity stockDealMonthEntity);

    /**
     * 批量插入数据
     *
     * @param list
     * @return
     */
    Integer batchInsert(List<StockDealMonthEntity> list);

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
     * @param stockDealMonthEntity
     * @return
     */
    StockDealMonthEntity getBySignalDate(StockDealMonthEntity stockDealMonthEntity);

    /**
     * 获取股票全部数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    List<StockDealMonthEntity> getTotalByStock(Integer stockId, Integer fqType);

    /**
     * 获根据起止日期取按天交易数据
     *
     * @param stockId
     * @param fqType
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockDealMonthEntity> getByDate(Integer stockId, Integer fqType, String startDate, String endDate);

    /**
     * 优化表
     *
     * @return
     */
    Boolean optimize();
}
