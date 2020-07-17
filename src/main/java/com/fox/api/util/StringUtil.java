package com.fox.api.util;

import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * 字符串工具类
 * @author lusongsong
 */
public class StringUtil {
    /**
     * list拼接成字符串
     * @param list
     * @param separator
     * @return
     */
    public static String listToString(List<String> list, String separator) {
        return StringUtils.join(list.toArray(), separator);
    }
}
