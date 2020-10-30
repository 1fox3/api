package com.fox.api.service.third.stock.sina.api;

import com.fox.api.dao.stock.entity.StockEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * k线图片
 *
 * @author lusongsong
 * @date 2020/3/5 18:13
 */
public class SinaKLineImage extends SinaStockBaseApi {
    /**
     * 分钟
     */
    public static final String DATE_TYPE_MINUTE = "min";
    /**
     * 天
     */
    public static final String DATE_TYPE_DAY = "daily";
    /**
     * 周
     */
    public static final String DATE_TYPE_WEEK = "weekly";
    /**
     * 月
     */
    public static final String DATE_TYPE_MONTH = "monthly";

    /**
     * 支持的k线类型
     */
    private static Map<String, String> dateTypeMap = new HashMap<String, String>() {{
        put("MINUTE", DATE_TYPE_MINUTE);
        put("DAY", DATE_TYPE_DAY);
        put("WEEK", DATE_TYPE_WEEK);
        put("MONTH", DATE_TYPE_MONTH);
    }};

    /**
     * 样例链接
     */
    private static String demoUrl = "http://image.sinajs.cn/newchart/{dateType}/n/{stockCode}.gif";

    /**
     * 获取k线图片
     *
     * @param stockEntity
     * @param dateType
     * @return
     */
    public String getKLineImageUrl(StockEntity stockEntity, String dateType) {
        dateType = dateType.toUpperCase();
        if (dateTypeMap.containsKey(dateType)) {
            return demoUrl.replace("{dateType}", dateTypeMap.get(dateType))
                    .replace("{stockCode}", SinaStockBaseApi.getSinaStockCode(stockEntity));
        }
        return "";
    }
}
