package com.alita.framework.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类.
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-13 13:47
 */
public class DateUtils {

    public static final String DATE_PATTERN = "yyyyMMdd";
    public static final String TIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * 时区 亚州/上海 东八区
     * <p>
     *    时区写法2：timezone = "GMT+8";
     *    时区写法3：timezone = "+8";
     * </p>
     */
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

//    private static final ZoneOffset zoneOffset = ZoneOffset.of("+8");
    /**
     * 时区偏移量
     */
    private static final ZoneOffset offset = ZonedDateTime.now(zoneId).getOffset();


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
     * @param date 日期参数
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String formatDateTime(Date date, String pattern) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 返回在指定时间的基础上后移对应秒之后的时间.
     *
     * @param date 时间
     * @param plusSeconds 单位：秒
     * @return 时间
     */
    public static Date addSeconds(Date date, int plusSeconds) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusSeconds(plusSeconds);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应分钟之后的时间.
     *
     * @param date 时间
     * @param plusMinutes 单位：分钟
     * @return 时间
     */
    public static Date addMinutes(Date date, int plusMinutes) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusMinutes(plusMinutes);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应小时之后的时间.
     *
     * @param date 时间
     * @param plusHours 单位：小时
     * @return 时间
     */
    public static Date addHours(Date date, int plusHours) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusHours(plusHours);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应天之后的时间.
     *
     * @param date 时间
     * @param plusDays 单位：天
     * @return 时间
     */
    public static Date addDays(Date date, int plusDays) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusDays(plusDays);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应周之后的时间.
     *
     * @param date 时间
     * @param plusWeeks 单位：周
     * @return 时间
     */
    public static Date addWeeks(Date date, int plusWeeks) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusWeeks(plusWeeks);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应月之后的时间.
     *
     * @param date 时间
     * @param plusMonths 单位：月
     * @return 时间
     */
    public static Date addMonths(Date date, int plusMonths) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusMonths(plusMonths);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上后移对应年之后的时间.
     *
     * @param date 时间
     * @param plusYears 单位：年
     * @return 时间
     */
    public static Date addYears(Date date, int plusYears) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).plusYears(plusYears);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应秒之后的时间.
     *
     * @param date 时间
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
     * @param date 时间
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
     * @param date 时间
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
     * @param date 时间
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
     * @param date 时间
     * @param minusWeeks 单位：周
     * @return 时间
     */
    public static Date minusWeeks(Date date, int minusWeeks) {
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId).minusWeeks(minusWeeks);
        return from(localDateTime);
    }

    /**
     * 返回在指定时间的基础上前移对应年之后的时间.
     *
     * @param date 时间
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
     * 调整到指定时间.
     *
     * <p>
     *  jdk8 以后新增 java.time 包对时间类的操作.
     *
     * <pre>
     *  {@code
     *      LocalDateTime localDateTime = LocalDateTime.now();
     *      localDateTime = localDateTime.toLocalDate().atTime(hour, minute);
     *  }
     *  或者
     *  {@code
     *      LocalDateTime localDateTime = LocalDateTime.now();
     *      localDateTime = localDateTime.with(ChronoField.MILLI_OF_DAY, 0);
     *  }
     * </pre>
     *
     * e.g.下周二上午10点整
     * <pre>
     *  {@code
     *      LocalDateTime localDateTime = LocalDateTime.now();
     *      localDateTime = localDateTime.plusWeeks(1).with(ChronoField.DAY_OF_WEEK, 2).with(ChronoField.MILLI_OF_DAY, 0).withHour(10);
     *  }
     * </pre>
     *
     * 	e.g.下一个周二上午10点整
     * 	<pre>
     * 	{@code
     * 	    LocalDate ld = LocalDate.now();
     * 	    LocalDateTime ldt = ld.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(10, 0);
     * 	    LocalDate ld = LocalDate.now();
     * 	    if(!ld.getDayOfWeek().equals(DayOfWeek.MONDAY)){
     * 	        ld = ld.plusWeeks(1);
     * 	    }
     * 	    LocalDateTime ldt = ld.with(ChronoField.DAY_OF_WEEK, 2).atTime(10, 0);
     * 	}
     * 	</pre>
     *
     *  e.g.明天最后一刻：2022-11-14T23:59:59.999999999
     *  <pre>
     *  {@code LocalDateTime ldt = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX);}
     *  或者
     *  {@code LocalDateTime ldt = LocalTime.MAX.atDate(LocalDate.now().plusDays(1));}
     *  </pre>
     *
     *  e.g.本月最后一天最后一刻
     *  <pre>
     *  {@code LocalDateTime ldt = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);}
     *  </pre>
     *
     *  e.g.下个月第一个周一的下午5点整
     *  <pre>
     *  {@code LocalDateTime ldt = LocalDate.now().plusMonths(1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)).atTime(17, 0);}
     *  </pre>
     *
     * @param hour 小时
     * @param minute 分钟
     * @param second 秒
     * @return 时间
     */
    public static LocalDateTime adjustDateTime(int hour, int minute, int second){
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(0);
        return localDateTime;
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
     * 获取两个时间的间隔.
     *
     * <p>
     *  e.g.period.getYears() + "年" + period.getMonths() + "月" + period.getDays() + "天"
     * </p>
     *
     * @param from 第一个时间
     * @param to 第二个时间
     * @return 间隔 Period
     */
    public static Period between(Date from, Date to) {
        Instant fromInstant = from.toInstant();
        Instant toInstant = to.toInstant();

        LocalDate fromDate = LocalDate.ofInstant(fromInstant, zoneId);
        LocalDate toDate = LocalDate.ofInstant(toInstant, zoneId);

        Period period = Period.between(fromDate, toDate);
        return period;
    }

    /**
     * 返回两个时间的毫秒数.
     *
     * <pre>
     *      long lateMinutes = Duration.between(LocalTime.of(11,0),LocalTime.now()).toMinutes();
     * </pre>
     *
     * @param from 第一个时间
     * @param to 第二个时间
     * @return 毫秒数
     */
    public static long betweenMinutes(Date from, Date to) {
        Instant fromInstant = from.toInstant();
        Instant toInstant = to.toInstant();

        LocalDateTime fromDate = LocalDateTime.ofInstant(fromInstant, zoneId);
        LocalDateTime toDate = LocalDateTime.ofInstant(toInstant, zoneId);

        long millis = Duration.between(fromDate, toDate).toMillis();
        return millis;
    }


}
