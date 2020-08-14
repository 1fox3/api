package com.fox.api.entity.dto.stock.realtime.rank;

import lombok.Data;

/**
 * 股票实时排行信息
 * @author lusongsong
 */
@Data
public class StockRealtimeRankInfoDto {
    /**
     * 股票id
     */
    private Integer stockId;
    /**
     * 股票代码
     */
    private String stockCode;
    /**
     * 股票名称
     */
    private String stockName;
    /**
     * 当前价格
     */
    private Double price;
    /**
     * 涨幅
     */
    private Double uptickRate;
    /**
     * 波动
     */
    private Double surgeRate;
    /**
     * 成交量
     */
    private Double dealNum;
    /**
     * 成交金额
     */
    private Double dealMoney;
}
