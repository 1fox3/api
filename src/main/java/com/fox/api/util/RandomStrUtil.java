package com.fox.api.util;

import java.util.Random;

/**
 * 随机字符串工具
 * @author lusongsong
 */
public class RandomStrUtil {
    /**
     * 随机字符串的类别
     */
    public final static String NUMBER_LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public final static String NUMBER = "0123456789";
    public final static String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public final static String LOW_LETTER = "abcdefghijklmnopqrstuvwxyz";
    public final static String UP_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 获取随机字符串
     * @param length
     * @param seed
     * @return
     */
    public static String getRandomStr(int length, String seed) {
        if (length <1 || null == seed || 0 == seed.length()) {
            return "";
        }
        int scope = seed.length();
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(scope);
            stringBuffer.append(seed.charAt(number));
        }
        return stringBuffer.toString();
    }
}
