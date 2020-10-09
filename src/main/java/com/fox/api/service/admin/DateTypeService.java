package com.fox.api.service.admin;

/**
 * 日期类型
 * @author lusongsong
 * @date 2020/10/5 16:52
 */
public interface DateTypeService {
    /**
     * 未知
     */
    Integer DATE_TYPE_UNKNOWN = 0;
    /**
     * 工作日
     */
    Integer DATE_TYPE_WORKDAY = 1;
    /**
     * 周末
     */
    Integer DATE_TYPE_WEEKEND = 2;
    /**
     * 假期
     */
    Integer DATE_TYPE_HOLIDAY = 3;
    /**
     * 调班
     */
    Integer DATE_TYPE_TRANSFER = 4;

    /**
     * 根据日期获取日期类型
     * @param dt
     * @return
     */
    Integer getByDate(String dt);
}
