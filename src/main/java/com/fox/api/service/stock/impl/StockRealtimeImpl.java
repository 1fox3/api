package com.fox.api.service.stock.impl;

import com.fox.api.constant.StockConst;
import com.fox.api.dao.stock.entity.StockEntity;
import com.fox.api.entity.dto.stock.realtime.StockRealtimeInfoDto;
import com.fox.api.entity.property.stock.StockCodeProperty;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class StockRealtimeImpl extends StockBaseImpl implements StockRealtimeService {

    /**
     * 获取实时信息
     * @param stockId
     * @return
     */
    @Override
    public StockRealtimePo info(Integer stockId) {
        if (this.stockRedisUtil.hHasKey(this.redisRealtimeStockInfoHash, stockId.toString())) {
            return (StockRealtimePo)this.stockRedisUtil.hGet(this.redisRealtimeStockInfoHash, stockId.toString());
        }
        SinaRealtime sinaRealtime = new SinaRealtime();
        StockRealtimePo stockRealtimePo = sinaRealtime.getRealtimeData(stockMapper.getById(stockId));
        return  stockRealtimePo;
    }

    /**
     * 获取实时线图
     * @param stockId
     * @return
     */
    @Override
    public StockRealtimeLinePo line(Integer stockId) {
        String redisKey = this.redisRealtimeStockLineSingle + stockId;
        StockRealtimeLinePo stockRealtimeLinePo = (StockRealtimeLinePo)this.stockRedisUtil.get(redisKey);
        if (null != stockRealtimeLinePo) {
            return stockRealtimeLinePo;
        }
        NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
        stockRealtimeLinePo = netsMinuteRealtime.getRealtimeData(this.getNetsStockInfoMap(stockId));
        if (null != stockRealtimeLinePo) {
            this.stockRedisUtil.set(redisKey, stockId, Long.valueOf(5));
        }
        return  stockRealtimeLinePo;
    }

    /**
     * 获取重要指标信息
     * @return
     */
    @Override
    public List<StockRealtimeInfoDto> topIndex() {
        List<StockRealtimeInfoDto> list = new LinkedList<>();
        List<StockCodeProperty> topIndexList = this.stockProperty.getTopIndex();
        for (StockCodeProperty stockCodeProperty : topIndexList) {
            if (!StockConst.SM_A_LIST.contains(stockCodeProperty.getStockMarket())) {
                continue;
            }
            StockEntity stockEntity = this.stockMapper.getByStockCode(
                    stockCodeProperty.getStockCode(),
                    stockCodeProperty.getStockMarket()
            );
            if (null != stockEntity && null != stockEntity.getId()) {
                Integer stockId = stockEntity.getId();
                StockRealtimePo stockRealtimePo = this.info(stockId);
                StockRealtimeInfoDto stockRealtimeInfoDto = new StockRealtimeInfoDto();
                BeanUtils.copyProperties(stockRealtimePo, stockRealtimeInfoDto);
                stockRealtimeInfoDto.setStockId(stockId);
                list.add(stockRealtimeInfoDto);
            }

        }
        return list;
    }

    /**
     * 获取增幅统计信息
     * @return
     */
    @Override
    public Map<String, Integer> uptickRateStatistics() {
        Object object = this.stockRedisUtil.get(this.stockRealtimeStockUptickRateStatistics);
        if (null == object) {
            return new HashMap<>(0);
        }
        return (Map<String, Integer>)object;
    }
}
