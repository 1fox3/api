package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.realtime.rank.StockRealtimeRankInfoDto;
import com.fox.api.entity.po.PageInfoPo;

import java.util.List;

public interface StockRealtimeRankService {
    List<StockRealtimeRankInfoDto> rank(String type, String sortType, PageInfoPo pageInfo);
}
