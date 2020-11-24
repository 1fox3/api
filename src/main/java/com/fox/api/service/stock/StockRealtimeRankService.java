package com.fox.api.service.stock;

import com.fox.api.entity.dto.stock.realtime.rank.StockRealtimeRankInfoDto;
import com.fox.api.entity.po.PageInfoPo;

import java.util.List;

/**
 * 股票实时交易数据排行相关
 *
 * @author lusongsong
 * @date 2020/3/31 17:43
 */
public interface StockRealtimeRankService {
    /**
     * 股票实时交易排行列表
     *
     * @param stockMarket
     * @param type
     * @param sortType
     * @param pageInfo
     * @return
     */
    List<StockRealtimeRankInfoDto> rank(Integer stockMarket, String type, String sortType, PageInfoPo pageInfo);
}
