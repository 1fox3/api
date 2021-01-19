package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockDealMinuteEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.api.sina.SinaPriceDealNumRatioApi;
import com.fox.spider.stock.constant.StockConst;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.po.sina.SinaPriceDealNumRatioPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 股票历史交易信息
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Service
public class StockOfflineImpl extends StockBaseImpl implements StockOfflineService {
    @Autowired
    StockRealtimeService stockRealtimeService;
    /**
     * 新浪股票价格成交占比接口
     */
    @Autowired
    SinaPriceDealNumRatioApi sinaPriceDealNumRatioApi;

    @Override
    public StockDealDayLineDto line(Integer stockId, String startDate) {
        return this.line(stockId, startDate, DateUtil.getCurrentDate());
    }

    @Override
    public StockDealDayLineDto line(Integer stockId, String startDate, String endDate) {
        return null;
    }

    /**
     * 获取价格成交比例
     *
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<SinaPriceDealNumRatioPo> dealRatio(Integer stockId, String startDate, String endDate) {
        StockEntity stockEntity = this.getStockEntity(stockId);
        return sinaPriceDealNumRatioApi.priceDealNumRatio(new StockVo(stockEntity.getStockCode(), stockEntity.getStockMarket()), startDate, endDate);
    }

    /**
     * 股票单天交易数据
     *
     * @param stockId
     * @return
     */
    @Override
    public List<List<Object>> day(Integer stockId) {
        return day(stockId, 0);
    }

    /**
     * 股票单天交易数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    @Override
    public List<List<Object>> day(Integer stockId, Integer fqType) {
        return null;
    }

    /**
     * 股票按周交易数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    @Override
    public List<List<Object>> week(Integer stockId, Integer fqType) {
        return null;
    }

    /**
     * 股票按月交易数据
     *
     * @param stockId
     * @param fqType
     * @return
     */
    @Override
    public List<List<Object>> month(Integer stockId, Integer fqType) {
        return null;
    }

    /**
     * 近5天交易日分钟成交数据
     *
     * @param stockId
     * @return
     */
    @Override
    public List<List<Object>> fiveDayMin(Integer stockId) {
        List<StockDealMinuteEntity> stockDealMinuteEntityList = stockDealMinuteMapper.len(stockId, 1210);

        Collections.reverse(stockDealMinuteEntityList);

        stockDealMinuteEntityList = null == stockDealMinuteEntityList ?
                new ArrayList<>() : stockDealMinuteEntityList;
        Boolean getRealtime = true;
        String lastDealDate = StockUtil.lastDealDate(StockConst.SM_A);
        if (stockDealMinuteEntityList.size() > 0) {
            StockDealMinuteEntity stockDealMinuteEntity = stockDealMinuteEntityList.get(
                    stockDealMinuteEntityList.size() - 1
            );
            if (null != stockDealMinuteEntity && null != stockDealMinuteEntity.getDt()
                    && stockDealMinuteEntity.getDt().equals(lastDealDate)) {
                getRealtime = false;
            }
        }

        if (getRealtime) {
            NetsRealtimeMinuteKLinePo netsRealtimeMinuteDealInfoPo = stockRealtimeService.line(stockId);
            if (null != netsRealtimeMinuteDealInfoPo && null != netsRealtimeMinuteDealInfoPo.getKlineData()) {
                List<NetsRealtimeMinuteNodeDataPo> netsRealtimeMinuteNodeDataPoList = netsRealtimeMinuteDealInfoPo.getKlineData();
                for (NetsRealtimeMinuteNodeDataPo netsRealtimeMinuteNodeDataPo : netsRealtimeMinuteNodeDataPoList) {
                    StockDealMinuteEntity stockDealMinuteEntity = new StockDealMinuteEntity();
                    stockDealMinuteEntity.setDt(lastDealDate);
                    stockDealMinuteEntity.setTime(netsRealtimeMinuteNodeDataPo.getTime());
                    stockDealMinuteEntity.setPrice(netsRealtimeMinuteNodeDataPo.getPrice());
                    stockDealMinuteEntity.setAvgPrice(netsRealtimeMinuteNodeDataPo.getAvgPrice());
                    stockDealMinuteEntity.setDealNum(netsRealtimeMinuteNodeDataPo.getDealNum());
                }
            }
        }

        Collections.reverse(stockDealMinuteEntityList);

        List<String> dtList = new LinkedList<>();
        List<List<Object>> fiveDayList = new LinkedList<>();
        for (StockDealMinuteEntity stockDealMinuteEntity : stockDealMinuteEntityList) {
            if (null == stockDealMinuteEntity) {
                continue;
            }
            if (!dtList.contains(stockDealMinuteEntity.getDt())) {
                dtList.add(stockDealMinuteEntity.getDt());
            }
            if (dtList.size() > 5) {
                break;
            }
            List<Object> oneMinList = new LinkedList<>();
            oneMinList.add(stockDealMinuteEntity.getDt());
            oneMinList.add(stockDealMinuteEntity.getTime());
            oneMinList.add(stockDealMinuteEntity.getPrice());
            oneMinList.add(stockDealMinuteEntity.getAvgPrice());
            oneMinList.add(stockDealMinuteEntity.getDealNum());
            fiveDayList.add(oneMinList);
        }

        Collections.reverse(fiveDayList);
        return fiveDayList;
    }
}
