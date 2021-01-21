package com.fox.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal工具类
 *
 * @author lusongsong
 * @date 2021/1/21 14:37
 */
public class BigDecimalUtil {
    /**
     * 计算比率
     *
     * @param bigDecimal1
     * @param bigDecimal2
     * @return
     */
    public static BigDecimal rate(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        return bigDecimal1.multiply(new BigDecimal(100)).divide(bigDecimal2, 2, RoundingMode.HALF_UP);
    }
}
