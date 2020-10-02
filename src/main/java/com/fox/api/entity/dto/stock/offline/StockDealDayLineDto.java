package com.fox.api.entity.dto.stock.offline;

import lombok.Data;

import java.util.List;

/**
 * 交易线图
 * @author lusongsong
 * @date 2020/4/9 16:55
 */
@Data
public class StockDealDayLineDto {
    /**
     * 股票代码
     */
    String stockCode;
    /**
     * 股票名称
     */
    String stockName;
    /**
     * 线图信息
     */
    List<StockDealDayDto> lineNode;
}
