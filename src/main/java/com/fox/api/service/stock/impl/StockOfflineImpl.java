package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.*;
import com.fox.api.entity.dto.stock.offline.StockDealDayDto;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.*;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.service.third.stock.sina.api.SinaDealRatio;
import com.fox.api.util.DateUtil;
import com.fox.api.util.StockUtil;
import com.fox.spider.stock.constant.StockConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 股票历史交易信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Service
public class StockOfflineImpl extends StockBaseImpl implements StockOfflineService {
    @Autowired
    StockRealtimeService stockRealtimeService;

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
     * @param stockId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<StockDealNumPo> dealRatio(Integer stockId, String startDate, String endDate) {
        SinaDealRatio sinaDealRatio = new SinaDealRatio();
        return sinaDealRatio.getDealRatio(this.getStockEntity(stockId), startDate, endDate);
    }

    /**
     * 股票单天交易数据
     * @param stockId
     * @return
     */
    @Override
    public List<List<Object>> day(Integer stockId) {
        return day(stockId, 0);
    }

    /**
     * 股票单天交易数据
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
            StockRealtimeLinePo stockRealtimeLinePo = stockRealtimeService.line(stockId);
            if (null != stockRealtimeLinePo && null != stockRealtimeLinePo.getLineNode()) {
                List<StockRealtimeNodePo> lineNodeList = stockRealtimeLinePo.getLineNode();
                for (StockRealtimeNodePo stockRealtimeNodePo : lineNodeList) {
                    StockDealMinuteEntity stockDealMinuteEntity = new StockDealMinuteEntity();
                    stockDealMinuteEntity.setDt(lastDealDate);
                    stockDealMinuteEntity.setTime(stockRealtimeNodePo.getTime());
                    stockDealMinuteEntity.setPrice(stockRealtimeNodePo.getPrice());
                    stockDealMinuteEntity.setAvgPrice(stockRealtimeNodePo.getAvgPrice());
                    stockDealMinuteEntity.setDealNum(stockRealtimeNodePo.getDealNum());
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
