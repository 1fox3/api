package com.fox.api.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具
 */
public class DateUtil {
    //年期格式
    public static final String YEAR_FORMAT_1 = "yyyy";

    //日期格式
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_2 = "yyyyMMdd";

    //时间格式
    public static final String TIME_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";

    //格式类
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    /**
     * 获取当前年份
     * @return
     */
    public static String getCurrentYear() {
        simpleDateFormat.applyPattern(YEAR_FORMAT_1);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 日期字符串转换格式
     * @param str
     * @param oldFormat
     * @param newFormat
     * @return
     */
    public static String dateStrFormatChange(String str, String oldFormat, String newFormat) {
        Date date = DateUtil.strToDate(str, oldFormat);
        simpleDateFormat.applyPattern(newFormat);
        return simpleDateFormat.format(date);
    }

    /**
     * 将特定格式的时间字符串转化为Date类型
     * @param str
     * @param format
     * @return
     */
    public static synchronized Date strToDate(String str, String format) {
        simpleDateFormat.applyPattern(format);
        ParsePosition parseposition = new ParsePosition(0);
        return simpleDateFormat.parse(str, parseposition);
    }

    /**
     * 将日期转成其他格式
     * @param date
     * @param format
     * @return
     */
    public static synchronized String dateToStr(Date date, String format) {
        simpleDateFormat.applyPattern(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取相对日期
     * @param diffYear
     * @param diffMonth
     * @param diffDay
     * @param format
     * @return
     */
    public static String getRelateDate(int diffYear, int diffMonth, int diffDay, String format) {
        simpleDateFormat.applyPattern(format);

        return getRelateDate(simpleDateFormat.format(new Date()), diffYear, diffMonth, diffDay, format);
    }

    /**
     * 获取相对日期
     * @param dateStr
     * @param diffYear
     * @param diffMonth
     * @param diffDay
     * @param format
     * @return
     */
    public static String getRelateDate(String dateStr, int diffYear, int diffMonth, int diffDay, String format) {
        Date date = DateUtil.strToDate(dateStr, format);
        simpleDateFormat.applyPattern(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, diffYear);
        calendar.add(Calendar.MONTH, diffMonth);
        calendar.add(Calendar.DATE, diffDay);
        Date targetDay = calendar.getTime();
        return simpleDateFormat.format(targetDay);
    }

    /**
     * 根据日期字符串获取日期
     * @param dateStr
     * @return
     */
    public static Date getDateFromStr(String dateStr) {
        return getDateFromStr(dateStr, DateUtil.DATE_FORMAT_1);
    }

    /**
     * 根据日期字符串获取日期
     * @param dateStr
     * @param format
     * @return
     */
    public static Date getDateFromStr(String dateStr, String format) {
        return getDateFromStr(dateStr, format, new Date());
    }

    /**
     * 根据日期字符串获取日期
     * @param dateStr
     * @param format
     * @param defaultDate
     * @return
     */
    public static Date getDateFromStr(String dateStr, String format, Date defaultDate) {
        simpleDateFormat.applyPattern(format);
        Date date;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            date = defaultDate;
        }
        return date;
    }
}
