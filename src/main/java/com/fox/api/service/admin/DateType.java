package com.fox.api.service.admin;

/**
 * 日期类型
 * @author lusongsong
 * @date 2020/10/5 16:52
 */
public interface DateType {
    /**
     * 根据日期获取日期类型
     * @param dt
     * @return
     */
    Integer getByDate(String dt);
}
