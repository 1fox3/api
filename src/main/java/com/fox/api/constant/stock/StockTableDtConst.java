package com.fox.api.constant.stock;

import java.util.HashMap;
import java.util.Map;

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
    public static final int TYPE_DEFAULT = 0;
    /**
     * 已备份
     */
    public static final int TYPE_BAK = 1;

    /**
     * 分钟交易信息表KEY
     */
    public static final int TABLE_KEY_DEAL_MINUTE = 1;
    /**
     * 分钟交易信息表名
     */
    public static final String TABLE_NAME_DEAL_MINUTE = "t_stock_deal_minute";
    /**
     * 按天价格交易量信息表
     */
    public static final int TABLE_PRICE_DEAL_NUM_DAY = 2;
    /**
     * 数据表id和表名对应
     */
    public static final Map<Integer, String> TABLE_DT_MAP = new HashMap<Integer, String>() {{
        put(TABLE_KEY_DEAL_MINUTE, TABLE_NAME_DEAL_MINUTE);
    }};
}
