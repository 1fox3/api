package com.fox.api.service.stock;

import com.fox.api.entity.po.third.stock.StockDayLinePo;

public interface StockOfflineService {
    StockDayLinePo line(Integer stockId);
    StockDayLinePo line(Integer stockId, String startDate);
    StockDayLinePo line(Integer stockId, Integer dayLen);
    StockDayLinePo line(Integer stockId, String startDate, String endDate);
}
