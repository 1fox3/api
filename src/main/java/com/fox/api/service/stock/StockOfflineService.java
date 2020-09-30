package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.StockDealNumPo;

import java.util.List;

/**
 * 股票历史交易信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public interface StockOfflineService {
    /**
     * 股票按天交易数据
     * @param stockId
     * @param startDate
     * @return
     */
    StockDealDayLineDto line(Integer stockId, String startDate);

    /**
     * 股票按天交易数据
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    StockDealDayLineDto line(Integer stockId, String startDate, String endDate);

    /**
     * 股票价格成交占比
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    List<StockDealNumPo> dealRatio(Integer stockId, String startDate, String endDate);

    /**
     * 股票单天交易数据
     * @param stockId
     * @return
     */
    List<List<Object>> day(Integer stockId);

    /**
     * 股票单天交易数据
     * @param stockId
     * @param fqType
     * @return
     */
    List<List<Object>> day(Integer stockId, Integer fqType);

}
