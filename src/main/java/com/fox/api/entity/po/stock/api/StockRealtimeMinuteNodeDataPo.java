package com.fox.api.entity.po.stock.api;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 股票最新交易日分钟交易数据
 *
 * @author lusongsong
 * @date 2021/1/22 16:06
 */
@Data
public class StockRealtimeMinuteNodeDataPo implements Serializable {
    /**
     * 时间
     */
    String time;
    /**
     * 价格
     */
    BigDecimal price;
    /**
     * 成交量
     */
    Long dealNum;
}
