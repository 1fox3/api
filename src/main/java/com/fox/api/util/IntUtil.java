package com.fox.api.util;

/**
 * 整数工具类
 *
 * @author lusongsong
 * @date 2021/1/18 14:22
 */
public class IntUtil {
    /**
     * 获取随机整数
     *
     * @return
     */
    public static int random() {
        return random(0, 100);
    }

    /**
     * 获取随机整数
     *
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        long randomNum = System.currentTimeMillis();
        return (int) (randomNum % (max - min) + min);
    }
}
