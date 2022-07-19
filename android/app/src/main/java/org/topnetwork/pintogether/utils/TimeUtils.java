package org.topnetwork.pintogether.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 日期时间工具类
 *
 * @author Start
 */
public class TimeUtils {

    public final static String yyyyMMdd_C = "yyyy年MM月dd日";

    public final static String yyyyMM_F = "yyyy-MM";
    public final static String yyyyMMdd_F = "yyyy-MM-dd";
    public static final String HHmmss_F = "HH:mm:ss";
    public final static String yyyyMMddHHmmss_F = "yyyy-MM-dd HH:mm:ss";
    public final static String yyyyMMddHHmmsssss_F = "yyyy-MM-dd HH:mm:ss,SSS";
    public final static String yyyyMMddTHHmmssZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public final static String yyyy = "yyyy";
    public final static String yyyyMM = "yyyyMM";
    public final static String yyyyMMdd = "yyyyMMdd";
    public final static String HHmmss = "HHmmss";
    public final static String yyyyMMddHH = "yyyyMMddHH";
    public final static String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public final static String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
    public final static String yyyyMMddHHmmssSSSZ = "yyyyMMddHHmmssSSSZ";
    public static final String yyyyMMddHHmmssZ = "yyyyMMddHHmmssZ";

    /**
     * 取得系统时间
     */
    public static String getSysTime(String pattern) {
        return formatSysTime(new SimpleDateFormat(pattern));
    }

    /**
     * 格式化系统时间
     */
    private static String formatSysTime(SimpleDateFormat format) {
        return format.format(Calendar.getInstance().getTime());
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date getDate(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    public static Date getHour(int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }

    public static boolean validTime(String str, String pattern) throws Exception {
        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date;
        try {
            date = (Date) formatter.parse(str);
        } catch (ParseException e) {
            throw new Exception();
        }
        return str.equals(formatter.format(date));
    }

    public static Date format(String str, String pattern) throws Exception {
        DateFormat formatter = new SimpleDateFormat(pattern);
        try {
            return (Date) formatter.parse(str);
        } catch (ParseException e) {
            throw new Exception();
        }
    }

    public static String getSysYear() {
        return getSysTime(yyyy);
    }

    public static String getSysTime() {
        return getSysTime(yyyyMMddHHmmss_F);
    }

    public static String getSysTimeS() {
        return getSysTime(yyyyMMddHHmmsssss_F);
    }

    public static String getSysTimeLong() {
        return getSysTime(yyyyMMddHHmmss);
    }

    public static String getSysTimeSLong() {
        return getSysTime(yyyyMMddHHmmssSSS);
    }

    public static String getSysdate() {
        return getSysTime(yyyyMMdd_F);
    }

    public static String getSysyearmonthInt() {
        return getSysTime(yyyyMM);
    }

    public static String getSysdateInt() {
        return getSysTime(yyyyMMdd);
    }

    public static String getSysdateTimeStart() {
        return getSysdate() + " 00:00:00";
    }

    public static String getSysdateTimeEnd() {
        return getSysdate() + " 23:59:59";
    }

    public static String getSysDateLocal() {
        return getSysTime(yyyyMMdd_C);
    }

    public static String getTimeFormat(String str) throws Exception {
        return format(format(str, yyyyMMddHHmmss), yyyyMMddHHmmss_F);
    }

    public static String getDateFormat(String str) throws Exception {
        return format(format(str, yyyyMMddHHmmss_F), yyyyMMdd_F);
    }

    public static String getDateFormatLocal(String str) throws Exception {
        return format(format(str, yyyyMMddHHmmss_F), yyyyMMdd_C);
    }

    /**
     * 获取前一天日期格式
     */
    public static String getTheDayBefore() {
        return format(getDate(-1), yyyyMMdd);
    }

    /**
     * 获取前一天日期格式
     */
    public static String getTheDayBefore_F() {
        return format(getDate(-1), yyyyMMdd_F);
    }

    public static String getDateFormat(int days) {
        return format(getDate(days), yyyyMMdd_F);
    }

    public static String getDateFormatLocal(int days) {
        return format(getDate(days), yyyyMMdd_C);
    }

    public static String getTimeFormatHour(int hours) {
        return format(getHour(hours), yyyyMMddHHmmss_F);
    }

    public static Date getHourTime(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getHourTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getTomorrowHourTime(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getTomorrowHourTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getThismonthTime(int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getNextmonthTime(int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static Date getSecond(int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    public static long subtract(String start) throws Exception {
        return format(getSysTimeS(), yyyyMMddHHmmsssss_F).getTime() - format(start, yyyyMMddHHmmsssss_F).getTime();
    }

    public static long subtract(String end, String start) throws Exception {
        return format(end, yyyyMMddHHmmsssss_F).getTime() - format(start, yyyyMMddHHmmsssss_F).getTime();
    }

    public static String getFormatTime(String pattern, String TimeZoneFormat) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
        return format.format(Calendar.getInstance().getTime());
    }

    public static String formatTime(String str, String strpattern, String pattern, String TimeZoneFormat) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(strpattern, Locale.ENGLISH);
        Date date = (Date) dateFormat.parse(str);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone(TimeZoneFormat));
        return format.format(date);
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @return
     */
    public static String timeStampToDate(long seconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static long dateToTimeStamp(String date_str, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date_str).getTime();
    }

    public static long convertStamp(SimpleDateFormat format, String time)  {
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return date.getTime();
        }
        return 0;

    }
    // 时间戳转换成日期
    public static String convertDate(SimpleDateFormat sdf, long stamp) {
        return sdf.format(new Date(stamp));
    }


}