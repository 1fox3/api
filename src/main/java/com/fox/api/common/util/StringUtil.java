package com.fox.api.common.util;

import java.util.List;

/**
 * 字符串工具类
 */
public class StringUtil {
    /**
     * list拼接成字符串
     * @param list
     * @param separator
     * @return
     */
    public static String listToString(List<String> list, String separator) {
        return org.apache.commons.lang.StringUtils.join(list.toArray(), separator);
    }
}
