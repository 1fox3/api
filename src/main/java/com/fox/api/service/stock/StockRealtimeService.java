package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.realtime.StockRealtimeInfoDto;
import com.fox.api.entity.po.third.stock.StockRealtimePo;
import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;

import java.util.List;

public interface StockRealtimeService {

    StockRealtimePo info(Integer stockId);

    StockRealtimeLinePo line(Integer stockId);

    List<StockRealtimeInfoDto> topIndex();
}
