package com.fox.api.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 股票相关静态常量
 * @author lusongsong
 * @date 2020/10/8 11:02
 */
public class StockConst {
    /**
     * 未知
     */
    public static final Integer SM_UNKNOWN = 0;
    /**
     * A股(沪深)
     */
    public static final Integer SM_A = 1;
    /**
     * 沪市
     */
    public static final Integer SM_SH = 1;
    /**
     * 深市
     */
    public static final Integer SM_SZ = 2;
    /**
     * 港式
     */
    public static final Integer SM_HK = 3;
    /**
     * A股列表
     */
    public static final List<Integer> SM_A_LIST = Arrays.asList(SM_SH, SM_SZ);

    /**
     * 未知
     */
    public static final Integer ST_UNKNOWN = 0;
    /**
     * 指数
     */
    public static final Integer ST_INDEX = 1;
    /**
     * 股票
     */
    public static final Integer ST_STOCK = 2;
}
