package com.qy.ticket.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/8 下午11:13
 **/
public class DateUtil {
    public static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat HH = new SimpleDateFormat("HH");

    /**
     * 获取当前时间几天前-/后+的时间
     *
     * @param sourceDate
     * @param num
     * @return
     */
    public static Date gerDayBefore(Date sourceDate, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + num);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几小时前-/后+的时间
     *
     * @param sourceDate
     * @param num
     * @return
     */
    public static Date gerHourBefore(Date sourceDate, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + num);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几分钟前-/后+的时间
     *
     * @param sourceDate
     * @param num
     * @return
     */
    public static Date gerMinuteBefore(Date sourceDate, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + num);
        return calendar.getTime();
    }

}
