package com.fox.api.service.stock.impl;

import com.fox.api.entity.dto.stock.realtime.StockRealtimeInfoDto;
import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

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
        StockRealtimePo stockRealtimePo = sinaRealtime.getRealtimeData(this.getSinaStockCode(stockId));
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

    @Override
    public List<StockRealtimeInfoDto> topIndex() {
        List<StockRealtimeInfoDto> list = new LinkedList<>();
        List<Integer> topIndexList = this.stockProperty.getTopIndex();
        for (Integer stockId : topIndexList) {
            StockRealtimePo stockRealtimePo = this.info(stockId);
            StockRealtimeInfoDto stockRealtimeInfoDto = new StockRealtimeInfoDto();
            BeanUtils.copyProperties(stockRealtimePo, stockRealtimeInfoDto);
            stockRealtimeInfoDto.setStockId(stockId);
            list.add(stockRealtimeInfoDto);
        }
        return list;
    }
}
