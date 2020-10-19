package com.fox.api.service.stock.impl;

import com.fox.api.dao.stock.entity.StockDealDayEntity;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.dao.stock.entity.StockPriceDayEntity;
import com.fox.api.entity.dto.stock.offline.StockDealDayDto;
import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.StockDayLinePo;
import com.fox.api.entity.po.third.stock.StockDealNumPo;
import com.fox.api.entity.po.third.stock.StockDealPo;
import com.fox.api.service.stock.StockOfflineService;
import com.fox.api.service.third.stock.nets.api.NetsDayLine;
import com.fox.api.service.third.stock.sina.api.SinaDealRatio;
import com.fox.api.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 股票历史交易信息
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
@Service
public class StockOfflineImpl extends StockBaseImpl implements StockOfflineService {

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
            StockDealDayEntity stockDealDayEntity = dealDayList.get(i);
            dayInfoList.add(stockPriceDayEntity.getDt());
            dayInfoList.add(stockPriceDayEntity.getOpenPrice());
            dayInfoList.add(stockPriceDayEntity.getClosePrice());
            dayInfoList.add(stockPriceDayEntity.getHighestPrice());
            dayInfoList.add(stockPriceDayEntity.getLowestPrice());
            dayInfoList.add(stockPriceDayEntity.getPreClosePrice());
            dayInfoList.add(stockDealDayEntity.getDealNum());
            dayInfoList.add(stockDealDayEntity.getDealMoney());
            dayInfoList.add(stockDealDayEntity.getCircEquity());
            dayInfoList.add(stockDealDayEntity.getTotalEquity());
            dayList.add(dayInfoList);
        }
        return dayList;
    }
}
