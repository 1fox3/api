package com.fox.api.service.stock;

import com.fox.api.service.third.stock.entity.StockDayLineEntity;

public interface StockOfflineService {
    StockDayLineEntity line(int stockId);
    StockDayLineEntity line(int stockId, String startDate);
    StockDayLineEntity line(int stockId, int dayLen);
    StockDayLineEntity line(int stockId, String startDate, String endDate);
}
