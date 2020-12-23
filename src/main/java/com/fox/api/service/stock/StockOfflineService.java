package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.spider.stock.entity.po.sina.SinaPriceDealNumRatioPo;

import java.util.List;

/**
 * 股票历史交易信息
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public interface StockOfflineService {
    /**
     * 股票按天交易数据
     *
     * @param stockId
     * @param startDate
     * @return
     */
    StockDealDayLineDto line(Integer stockId, String startDate);

    /**
     * 股票按天交易数据
     *
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    StockDealDayLineDto line(Integer stockId, String startDate, String endDate);

    /**
     * 股票价格成交占比
     *
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    List<SinaPriceDealNumRatioPo> dealRatio(Integer stockId, String startDate, String endDate);

    /**
     * 股票单天交易数据
     *
     * @param stockId
     * @return
     */
    List<List<Object>> day(Integer stockId);

    /**
     * 股票按天交易数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    List<List<Object>> day(Integer stockId, Integer fqType);

    /**
     * 股票按周交易数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    List<List<Object>> week(Integer stockId, Integer fqType);

    /**
     * 股票按月交易数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    List<List<Object>> month(Integer stockId, Integer fqType);

    /**
     * 近5天交易日分钟成交数据
     *
     * @param stockId
     * @return
     */
    List<List<Object>> fiveDayMin(Integer stockId);
}
