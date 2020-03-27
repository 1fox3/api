package com.fox.api.service.stock;

import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;

public interface StockRealtimeService {

    StockRealtimePo info(int stockId);

    StockRealtimeLinePo line(int stockId);
}
