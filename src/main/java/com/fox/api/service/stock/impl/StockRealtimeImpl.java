package com.fox.api.service.stock.impl;

import com.fox.api.service.stock.StockRealtimeService;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import com.fox.api.service.third.stock.nets.api.NetsMinuteRealtime;
import com.fox.api.service.third.stock.sina.api.SinaRealtime;
import org.springframework.stereotype.Service;

@Service
public class StockRealtimeImpl extends StockBaseImpl implements StockRealtimeService {

    @Override
    public StockRealtimePo info(int stockId) {
        SinaRealtime sinaRealtime = new SinaRealtime();
        return sinaRealtime.getRealtimeData(this.getSinaStockCode(stockId));
    }

    @Override
    public StockRealtimeLinePo line(int stockId) {
        NetsMinuteRealtime netsMinuteRealtime = new NetsMinuteRealtime();
        return netsMinuteRealtime.getRealtimeData(this.getNetsStockInfoMap(stockId));
    }
}
