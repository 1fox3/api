package com.fox.api.service.stock;

import com.fox.api.service.third.stock.entity.StockRealtimeEntity;
import com.fox.api.service.third.stock.entity.StockRealtimeLineEntity;

public interface StockRealtimeService {

    StockRealtimeEntity info(int stockId);

    StockRealtimeLineEntity line(int stockId);
}
