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
        StockDealDayLineDto stockDealDayLineDto = new StockDealDayLineDto();
        StockEntity stockEntity = this.stockMapper.getById(stockId);
        if (null == stockEntity) {
            return stockDealDayLineDto;
        }
        stockDealDayLineDto.setStockName(stockEntity.getStockName());
        stockDealDayLineDto.setStockCode(stockEntity.getStockCode());

        List<StockPriceDayEntity> priceDayList = this.stockPriceDayMapper.getByDate(
                0, stockId, startDate, endDate
        );
        List<StockDealDayDto> stockDealDayList = new LinkedList<>();
        if (null != priceDayList && 0 < priceDayList.size()) {
            List<StockDealDayEntity> dealDayList = this.stockDealDayMapper.getByDate(
                    stockId, startDate, endDate
            );
            Map<String, StockDealDayEntity> stockDealDayMap = new HashMap<>(priceDayList.size());
            if (null != dealDayList && 0 < dealDayList.size()) {
                for (StockDealDayEntity stockDealDayEntity : dealDayList) {
                    stockDealDayMap.put(stockDealDayEntity.getDt(), stockDealDayEntity);
                }
            }

            for (StockPriceDayEntity stockPriceDayEntity : priceDayList) {
                StockDealDayDto stockDealDayDto = new StockDealDayDto();
                BeanUtils.copyProperties(stockPriceDayEntity, stockDealDayDto);
                if (stockDealDayMap.containsKey(stockPriceDayEntity.getDt())) {
                    StockDealDayEntity stockDealDayEntity = stockDealDayMap.get(stockPriceDayEntity.getDt());
                    if (null != stockDealDayEntity) {
                        BeanUtils.copyProperties(stockDealDayEntity, stockDealDayDto);
                    }
                }
                stockDealDayList.add(stockDealDayDto);
            }
            stockDealDayLineDto.setLineNode(stockDealDayList);
            return stockDealDayLineDto;
        } else {
            NetsDayLine netsDayLine = new NetsDayLine();
            StockDayLinePo stockDayLinePo = netsDayLine.getDayLine(this.getNetsStockInfoMap(stockId), startDate, endDate);
            List<StockDealPo> stockDealPos = stockDayLinePo.getLineNode();
            if (null != stockDealPos && 0 < stockDealPos.size()) {
                for (StockDealPo stockDealPo : stockDealPos) {
                    StockDealDayDto stockDealDayDto = new StockDealDayDto();
                    BeanUtils.copyProperties(stockDealPo, stockDealDayDto);
                    stockDealDayDto.setDt(stockDealPo.getDateTime());
                    stockDealDayList.add(stockDealDayDto);
                }
            }
            stockDealDayLineDto.setLineNode(stockDealDayList);
        }

        return stockDealDayLineDto;
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
        List<List<Object>> dayList = new LinkedList<>();
        List<StockPriceDayEntity> priceDayList = this.stockPriceDayMapper.getTotalByStock(fqType, stockId);
        List<StockDealDayEntity> dealDayList = this.stockDealDayMapper.getTotalByStock(stockId);
        for (int i = 0; i < priceDayList.size(); i++) {
            List<Object> dayInfoList = new LinkedList<>();
            StockPriceDayEntity stockPriceDayEntity = priceDayList.get(i);
            dayInfoList.add(stockPriceDayEntity.getDt());
            dayInfoList.add(stockPriceDayEntity.getOpenPrice());
            dayInfoList.add(stockPriceDayEntity.getClosePrice());
            dayInfoList.add(stockPriceDayEntity.getHighestPrice());
            dayInfoList.add(stockPriceDayEntity.getLowestPrice());
            dayInfoList.add(stockPriceDayEntity.getPreClosePrice());
            if (i < dealDayList.size() - 1) {
                StockDealDayEntity stockDealDayEntity = dealDayList.get(i);
                dayInfoList.add(stockDealDayEntity.getDealNum());
                dayInfoList.add(stockDealDayEntity.getDealMoney());
                dayInfoList.add(stockDealDayEntity.getCircEquity());
                dayInfoList.add(stockDealDayEntity.getTotalEquity());
            } else {
                dayInfoList.add(BigDecimal.ZERO);
                dayInfoList.add(BigDecimal.ZERO);
                dayInfoList.add(BigDecimal.ZERO);
                dayInfoList.add(BigDecimal.ZERO);
            }
            dayList.add(dayInfoList);
        }
        return dayList;
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
        List<List<Object>> weekList = new LinkedList<>();
        List<StockPriceWeekEntity> priceWeekList = this.stockPriceWeekMapper.getTotalByStock(fqType, stockId);
        List<StockDealWeekEntity> dealWeekList = this.stockDealWeekMapper.getTotalByStock(stockId);
        for (int i = 0; i < priceWeekList.size(); i++) {
            List<Object> weekInfoList = new LinkedList<>();
            StockPriceWeekEntity stockPriceWeekEntity = priceWeekList.get(i);
            weekInfoList.add(stockPriceWeekEntity.getDt());
            weekInfoList.add(stockPriceWeekEntity.getOpenPrice());
            weekInfoList.add(stockPriceWeekEntity.getClosePrice());
            weekInfoList.add(stockPriceWeekEntity.getHighestPrice());
            weekInfoList.add(stockPriceWeekEntity.getLowestPrice());
            weekInfoList.add(stockPriceWeekEntity.getPreClosePrice());
            if (i < dealWeekList.size() - 1) {
                StockDealWeekEntity stockDealWeekEntity = dealWeekList.get(i);
                weekInfoList.add(stockDealWeekEntity.getDealNum());
                weekInfoList.add(stockDealWeekEntity.getDealMoney());
                weekInfoList.add(stockDealWeekEntity.getCircEquity());
                weekInfoList.add(stockDealWeekEntity.getTotalEquity());
            } else {
                weekInfoList.add(BigDecimal.ZERO);
                weekInfoList.add(BigDecimal.ZERO);
                weekInfoList.add(BigDecimal.ZERO);
                weekInfoList.add(BigDecimal.ZERO);
            }
            weekList.add(weekInfoList);
        }
        return weekList;
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
        List<List<Object>> monthList = new LinkedList<>();
        List<StockPriceMonthEntity> priceMonthList = this.stockPriceMonthMapper.getTotalByStock(fqType, stockId);
        List<StockDealMonthEntity> dealMonthList = this.stockDealMonthMapper.getTotalByStock(stockId);
        for (int i = 0; i < priceMonthList.size(); i++) {
            List<Object> monthInfoList = new LinkedList<>();
            StockPriceMonthEntity stockPriceMonthEntity = priceMonthList.get(i);
            monthInfoList.add(stockPriceMonthEntity.getDt());
            monthInfoList.add(stockPriceMonthEntity.getOpenPrice());
            monthInfoList.add(stockPriceMonthEntity.getClosePrice());
            monthInfoList.add(stockPriceMonthEntity.getHighestPrice());
            monthInfoList.add(stockPriceMonthEntity.getLowestPrice());
            monthInfoList.add(stockPriceMonthEntity.getPreClosePrice());
            if (i < dealMonthList.size() - 1) {
                StockDealMonthEntity stockDealMonthEntity = dealMonthList.get(i);
                monthInfoList.add(stockDealMonthEntity.getDealNum());
                monthInfoList.add(stockDealMonthEntity.getDealMoney());
                monthInfoList.add(stockDealMonthEntity.getCircEquity());
                monthInfoList.add(stockDealMonthEntity.getTotalEquity());
            } else {
                monthInfoList.add(BigDecimal.ZERO);
                monthInfoList.add(BigDecimal.ZERO);
                monthInfoList.add(BigDecimal.ZERO);
                monthInfoList.add(BigDecimal.ZERO);
            }
            monthList.add(monthInfoList);
        }
        return monthList;
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
