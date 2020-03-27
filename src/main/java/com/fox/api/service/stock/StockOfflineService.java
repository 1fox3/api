package com.fox.api.service.stock;

import com.fox.api.entity.po.third.stock.StockDayLinePo;

public interface StockOfflineService {
    StockDayLinePo line(int stockId);
    StockDayLinePo line(int stockId, String startDate);
    StockDayLinePo line(int stockId, int dayLen);
    StockDayLinePo line(int stockId, String startDate, String endDate);
}
