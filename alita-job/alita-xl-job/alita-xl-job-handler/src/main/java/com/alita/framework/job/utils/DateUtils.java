package com.alita.framework.job.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间工具类
 */
public class DateUtils {

    public static final String DATE_PATTERN = "yyyyMMdd";
    public static final String TIME_PATTERN = "yyyyMMddHHmmss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区 亚州/上海 东八区
     * <p>
     * 时区写法2：timezone = "GMT+8";
     * 时区写法3：timezone = "+8";
     * </p>
     */
    public static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    /**
     * 时区偏移量
     */
    public static final ZoneOffset offset = ZonedDateTime.now(zoneId).getOffset();

    private static final ThreadLocal<Map<String, DateFormat>> dateFormatThreadLocal = new ThreadLocal<Map<String, DateFormat>>();


    private static DateFormat getDateFormat(String pattern) {
        if (pattern == null || pattern.trim().length() == 0) {
            throw new IllegalArgumentException("pattern cannot be empty.");
        }

        Map<String, DateFormat> dateFormatMap = dateFormatThreadLocal.get();
        if (dateFormatMap != null && dateFormatMap.containsKey(pattern)) {
            return dateFormatMap.get(pattern);
        }

        synchronized (dateFormatThreadLocal) {
            if (dateFormatMap == null) {
                dateFormatMap = new HashMap();
            }
            dateFormatMap.put(pattern, new SimpleDateFormat(pattern));
            dateFormatThreadLocal.set(dateFormatMap);
        }

        return dateFormatMap.get(pattern);
    }


    /**
     * 返回当前时间
     *
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String getCurrentTime(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 返回当前日期.
     *
     * @return 日期字符串
     */
    public static String getCurrentDate() {
        LocalDate localDate = LocalDate.now();
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }

    /**
     * 格式化指定日期.
     *
     * @param date    日期参数
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String formatDateTime(Date date, String pattern) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将字符串时间转为 date 类型.
     *
     * @param dateString 时间
     * @return Date
     * @throws ParseException
     */
    public static Date parseDateTime(String dateString) throws ParseException {
        return parse(dateString, DATETIME_FORMAT);
    }

    /**
     * 将字符串时间转为 date 类型.
     *
     * @param dateString 时间
     * @param pattern 格式
     * @return Dates
     * @throws ParseException
     */
    public static Date parse(String dateString, String pattern) throws ParseException {
        Date date = getDateFormat(pattern).parse(dateString);
        return date;
    }

    /**
     * 返回在指定时间的基础上后移对应秒之后的时间.
     *
     * @param date        时间
     * @param plusSeconds 单位：秒
     * @return 时间
     */
    public static Date plusSeconds(Date date, int plusSeconds) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusSeconds(plusSeconds);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应分钟之后的时间.
     *
     * @param date        时间
     * @param plusMinutes 单位：分钟
     * @return 时间
     */
    public static Date plusMinutes(Date date, int plusMinutes) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusMinutes(plusMinutes);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应小时之后的时间.
     *
     * @param date      时间
     * @param plusHours 单位：小时
     * @return 时间
     */
    public static Date plusHours(Date date, int plusHours) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusHours(plusHours);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应天之后的时间.
     *
     * @param date     时间
     * @param plusDays 单位：天
     * @return 时间
     */
    public static Date plusDays(Date date, int plusDays) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusDays(plusDays);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应周之后的时间.
     *
     * @param date      时间
     * @param plusWeeks 单位：周
     * @return 时间
     */
    public static Date plusWeeks(Date date, int plusWeeks) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusWeeks(plusWeeks);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应月之后的时间.
     *
     * @param date       时间
     * @param plusMonths 单位：月
     * @return 时间
     */
    public static Date plusMonths(Date date, int plusMonths) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusMonths(plusMonths);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应年之后的时间.
     *
     * @param date      时间
     * @param plusYears 单位：年
     * @return 时间
     */
    public static Date plusYears(Date date, int plusYears) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusYears(plusYears);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应秒之后的时间.
     *
     * @param date         时间
     * @param minusSeconds 单位：秒
     * @return 时间
     */
    public static Date minusSeconds(Date date, int minusSeconds) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusSeconds(minusSeconds);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应分钟之后的时间.
     *
     * @param date         时间
     * @param minusMinutes 单位：分钟
     * @return 时间
     */
    public static Date minusMinutes(Date date, int minusMinutes) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusMinutes(minusMinutes);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应小时之后的时间.
     *
     * @param date       时间
     * @param minusHours 单位：小时
     * @return 时间
     */
    public static Date minusHours(Date date, int minusHours) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusHours(minusHours);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应天数之后的时间.
     *
     * @param date      时间
     * @param minusDays 单位：天
     * @return 时间
     */
    public static Date minusDays(Date date, int minusDays) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusDays(minusDays);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应周之后的时间.
     *
     * @param date       时间
     * @param minusWeeks 单位：周
     * @return 时间
     */
    public static Date minusWeeks(Date date, int minusWeeks) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusWeeks(minusWeeks);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应月之后的时间.
     *
     * @param date        时间
     * @param minusMonths 单位：月
     * @return 时间
     */
    public static Date minusMonths(Date date, int minusMonths) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusMonths(minusMonths);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应年之后的时间.
     *
     * @param date       时间
     * @param minusYears 单位：年
     * @return 时间
     */
    public static Date minusYears(Date date, int minusYears) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusYears(minusYears);
        return from(localDateTime);
    }

    private static Date from(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }

    /**
     * 获取当前时间的毫秒数.
     *
     * <p>
     * 与 {@code System.currentTimeMillis()} 返回的值相同.
     *
     * @return 毫秒数
     */
    public static long toEpochMilli() {
        return LocalDateTime.now().toInstant(offset).toEpochMilli();
    }

    /**
     * 获取指定日期的毫秒数.
     *
     * @param date 格式必须是：yyyy-MM-dd，否则将抛出异常.
     * @return 毫秒数
     */
    public static long toEpochMilli(String date) {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.parse(date), LocalTime.MIN);
        return dateTime.toInstant(offset).toEpochMilli();
    }

}
