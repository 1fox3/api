package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.offline.StockDealDayLineDto;
import com.fox.api.entity.po.third.stock.StockDealNumPo;

import java.util.List;

public interface StockOfflineService {
    StockDealDayLineDto line(Integer stockId, String startDate);
    StockDealDayLineDto line(Integer stockId, String startDate, String endDate);
    List<StockDealNumPo> dealRatio(Integer stockId, String startDate, String endDate);
}
