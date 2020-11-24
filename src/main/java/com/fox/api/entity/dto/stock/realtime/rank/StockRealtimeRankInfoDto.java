package com.fox.api.entity.dto.stock.realtime.rank;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票实时排行信息
 *
 * @author lusongsong
 * @date 2020/3/31 17:45
 */
@Data
public class StockRealtimeRankInfoDto {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 当前价格
     */
    BigDecimal currentPrice;
    /**
     * 涨幅
     */
    BigDecimal uptickRate;
    /**
     * 波动
     */
    BigDecimal surgeRate;
    /**
     * 成交量
     */
    Long dealNum;
    /**
     * 成交金额
     */
    BigDecimal dealMoney;
}
