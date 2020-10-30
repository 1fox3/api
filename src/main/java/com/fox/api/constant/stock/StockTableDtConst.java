package com.fox.api.constant.stock;

/**
 * 股票数据表枚举值
 *
 * @author lusongsong
 * @date 2020/10/30 17:01
 */
public class StockTableDtConst {
    /**
     * 未备份类型
     */
    public static final Integer TYPE_DEFAULT = 0;
    /**
     * 已备份
     */
    public static final Integer TYPE_BAK = 1;

    /**
     * 分钟价格交易表
     */
    public static final Integer TABLE_PRICE_MINUTE = 1;
    /**
     * 按天价格交易量信息表
     */
    public static final Integer TABLE_PRICE_DEAL_NUM_DAY = 2;
}
