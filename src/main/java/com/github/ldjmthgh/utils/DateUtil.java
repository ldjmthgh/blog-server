package com.github.ldjmthgh.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: ldj
 * @Date: 2021/3/2 22:30
 */
public final class DateUtil {
    private DateUtil() {
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final DateFormat DATA_FILE_FORMAT = new SimpleDateFormat("yyyyMMddhhmmss");
    private static final DateFormat DATA_DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");

    /**
     * 一般时间格式化
     * 
     * @param date
     * @return
     */
    public static String simpleDateFormat(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 获取当前时间的字符处格式化输出
     * 
     * @return 当前时间的字符处格式化输出
     */
    public static String getCurrentTimeStr() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取当前年月日
     * 
     * @return 年月日
     */
    public static String getCurrentDayStr() {
        return DATA_DAY_FORMAT.format(new Date());
    }

    /**
     * 获取当前时间的字符处格式化输出
     * 
     * @return 当前时间的字符处格式化输出
     */
    public static String getCurrentTimeFileStr() {
        return DATA_FILE_FORMAT.format(new Date());
    }

    /**
     * 获取当前年份
     * 
     * @return 当前年
     */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取当前年份
     * 
     * @return 当前月
     */
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    /**
     * 根据指定格式的时间字符串反转为日期
     * 
     * @param timeStr 日期字符串
     * @return 日期
     * @throws ParseException 日期的字符串格式不符
     */
    public static Date parseDateStr(String timeStr) throws ParseException {
        return DATE_FORMAT.parse(timeStr);
    }

    /**
     * 根据指定格式的时间字符串反转为日期
     * 
     * @param timeStr 日期字符串
     * @return 日期
     * @throws ParseException 日期的字符串格式不符
     */
    public static Date parseDateFileStr(String timeStr) throws ParseException {
        return DATA_FILE_FORMAT.parse(timeStr);
    }
}
