package com.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zyl
 * @date 2020/6/19 20:07
 * @require 和FilterUtils一样，需要根据实际需求对毫秒值进行特殊处理
 */
public class TimeUtil {

    /**
     * 得到开始时间和结束时间以及对应的毫秒值
     * 如果传入的开始时间为空，那么开始时间返回“历史”两个字，且计算时用Constant.HISTORY_MONTH定义的最早历史月份
     *
     * @param startMonth yyyy-MM 空值则为历史
     * @param endMonth   yyyy-MM
     */
    public static Map<String, String> getMillsAndMonthOfHistory(String startMonth, String endMonth) {
        String format = "yyyy-MM";
        long startMills, endMills;
        String startValueName = startMonth;
        // 开始时间为""，就设置为历史最早时间
        if (StrUtil.isBlank(startMonth)) {
            startMonth = DateUtil.format(DateUtil.parse(Constant.HISTORY_MONTH, format), format);
            startValueName = "历史";
        }
        // 获取开始月份的1号0点的毫秒数 毫秒数：原始数据里面是13位的时间戳
        startMills = DateUtil.parse(startMonth, format).getTime();
        // 结束时间为空，设置为本月
        if (StrUtil.isBlank(endMonth)) {
            endMonth = DateUtil.format(DateUtil.date(), format);
        }
        // 结束时间的毫秒数为结束月份的最后一天的23：59：59；毫秒数：原始数据里面是13位的时间戳
        endMills = DateUtil.parse(DateUtil.format(DateUtil.offsetMonth(DateUtil.parse(endMonth, format), 1), format), new SimpleDateFormat(format)).getTime() - Constant.MILLS;

        Map<String, String> map = new HashMap<>(8);
        map.put("startMonth", startValueName);
        map.put("endMonth", endMonth);
        map.put("startMills", startMills + "");
        map.put("endMills", endMills + "");
        return map;
    }

    /**
     * 得到开始时间和结束时间以及对应的毫秒值
     * 如果传入的开始时间为空，那么开始时间返回“历史”两个字，且计算时用Constant.HISTORY_DAY定义的最早历史时间
     *
     * @param startDay yyyy-MM-dd 空值则为历史
     * @param endDay   yyyy-MM-dd
     */
    public static Map<String, String> getMillsAndDayOfHistory(String startDay, String endDay) {
        String format = "yyyy-MM-dd";
        long startMills, endMills;
        // 开始时间为""，就设置为上一个月
        String startValueName = startDay;
        if (StrUtil.isBlank(startDay)) {
            startDay = DateUtil.format(DateUtil.parse(Constant.HISTORY_DAY, format), format);
            startValueName = "历史";
        }
        // 获取开始月份的1号0点的毫秒数 毫秒数：原始数据里面是13位的时间戳
        startMills = DateUtil.parse(startDay, format).getTime();
        // 结束时间为空，设置为本月
        if (StrUtil.isBlank(startDay)) {
            endDay = DateUtil.format(DateUtil.date(), format);
        }
        // 结束时间的毫秒数为本年本月本日的23：59：59；毫秒数：原始数据里面是13位的时间戳
        endMills = DateUtil.parse(DateUtil.format(DateUtil.offsetDay(DateUtil.parse(endDay, format), 1), format), new SimpleDateFormat(format)).getTime() - Constant.MILLS;
        Map<String, String> map = new HashMap<>(8);
        map.put("startDay", startValueName);
        map.put("endDay", endDay);
        map.put("startMills", startMills + "");
        map.put("endMills", endMills + "");
        return map;
    }

    /**
     * 如果传入时间为空，则返回本日
     *
     * @param date yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static String getDay(String date) {
        if ("".equals(date) || date == null) {
            return DateUtil.today();
        }
        return date;
    }

    /**
     * 没有传year过来，就默认为本年
     *
     * @param year yyyy
     * @return yyyy
     */
    public static String getYear(String year) {
        if ("".equals(year) || year == null) {
            return DateUtil.thisYear() + "";
        }
        return year;
    }

    /**
     * 没有传month，就默认为本月
     *
     * @param month  yyyy-MM的月份
     * @param format yyyy-MM
     * @return yyyy-MM
     */
    public static String getMonth(String month, String format) {
        if (StrUtil.isBlank(month)) {
            month = DateUtil.format(DateUtil.date(), format);
        }
        return month;
    }

    /**
     * 没有传quarter，就默认为本季度
     *
     * @param quarter 季度，只可选：1，2，3，4
     * @return 季度的int
     */
    public static String getQuarter(String quarter) {
        if (quarter == null || "".equals(quarter)) {
            int month = Integer.parseInt(DateUtil.format(DateUtil.date(), "MM"));
            if (month <= 3) {
                quarter = "1";
            } else if (month <= 6) {
                quarter = "2";
            } else if (month <= 9) {
                quarter = "3";
            } else {
                quarter = "4";
            }
        }
        return quarter;
    }

    /**
     * 将传入的季度1，2，3，4转换为对应的一，二，三，四
     *
     * @param quarter 季度，只可选：1，2，3，4
     * @return 一，二，三，四
     */
    public static String getQuarterString(String quarter) {
        if ("1".equals(quarter)) {
            return "一";
        } else if ("2".equals(quarter)) {
            return "二";
        } else if ("3".equals(quarter)) {
            return "三";
        } else {
            return "四";
        }
    }

    /**
     * 得到某年的每个月份的最后一天的毫秒值
     *
     * @param year yyyy
     * @return 一共13个数据，12月，但是有一个begin代表该年开始的毫秒值
     */
    public static Map<String, Long> getEveryMonthMillsOfYear(String year) {
        Map<String, Long> map = new LinkedHashMap<>();
        DateTime yearBegin = DateUtil.parse(year, "yyyy");
        String[] months = {"begin", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
        for (int i = 0; i < 13; i++) {
            map.put(months[i], DateUtil.offsetMonth(yearBegin, i).getTime() - Constant.MILLS);
        }
        return map;

    }

    /**
     * 拼接该年该季度的最后一个月，例如：2020year，1quarter，return 2020-3
     *
     * @param year    yyyy
     * @param quarter 只可用：1,2,3,4
     * @return yyyy-MM
     */
    private static String getYearMonthOfYearAndQuarter(String year, String quarter) {
        if ("1".equals(quarter)) {
            year += "-03";
        } else if ("2".equals(quarter)) {
            year += "-06";
        } else if ("3".equals(quarter)) {
            year += "-09";
        } else {
            year += "-12";
        }
        return year;
    }

    /**
     * 获取year的第quarter季度的最后一天
     *
     * @param year    年
     * @param quarter 季度，1，2，3，4的字符串
     * @return 2020，4->2020-12-31
     */
    public static String getLastDayOfThisQuarter(String year, String quarter) {
        year = getYearMonthOfYearAndQuarter(year, quarter);
        return DateUtil.formatDate(DateUtil.offsetDay(DateUtil.offsetMonth(DateUtil.parse(year, "yyyy-MM"), 1), -1));
    }

    /**
     * 得到某年某个季度的最后一天的结束时的毫秒值
     *
     * @param year    yyyy
     * @param quarter 只可用：1，2，3，4
     * @return ms毫秒值
     */
    public static long getLastDayEndMillsOfThisQuarter(String year, String quarter) {
        String day = getLastDayOfThisQuarter(year, quarter);
        return DateUtil.offsetDay(DateUtil.parse(day, "yyyy-MM-dd"), 1).getTime() - Constant.MILLS;
    }

    /**
     * 得到上个季度的最后一天
     *
     * @param year    yyyy
     * @param quarter 只可用：1，2，3，4
     * @return yyyy-MM-dd
     */
    public static String getLastDayOfLastQuarter(String year, String quarter) {
        quarter = String.valueOf((Integer.parseInt(quarter) - 1));
        // 因为1-1=0，就是去年了，而判断里面写着else = 4，所以0是4，year要-1
        if ("0".equals(quarter)) {
            year = String.valueOf(Integer.parseInt(year) - 1);
        }
        return getLastDayOfThisQuarter(year, quarter);
    }

    /**
     * 得到上一个季度的最后一天结束的毫秒值
     *
     * @param year    yyyy
     * @param quarter 只可用：1，2，3，4
     * @return 毫秒值
     */
    public static long getLastDayEndMillsOfLastQuarter(String year, String quarter) {
        String day = getLastDayOfLastQuarter(year, quarter);
        String format = "yyyy-MM-dd";
        return DateUtil.parse(DateUtil.format(DateUtil.offsetDay(DateUtil.parse(day, format), 1), format), new SimpleDateFormat(format)).getTime() - Constant.MILLS;
    }

    /**
     * 去年最后一天
     *
     * @param year yyyy
     * @return yyyy-MM-dd
     */
    public static String getLastDayOfLastYear(String year) {
        return DateUtil.format(DateUtil.offsetDay(DateUtil.parse(year, "yyyy"), -1), "yyyy-MM-dd");
    }

    /**
     * 去年最后一天结束的毫秒值
     *
     * @param year yyyy
     * @return 毫秒值
     */
    public static long getLastDayEndMillsOfLastYear(String year) {
        return DateUtil.parse((Integer.parseInt(year)) + "", "yyyy").getTime() - Constant.MILLS;
    }

    /**
     * 该年最后一天
     *
     * @param year yyyy
     * @return yyyy-MM-dd
     */
    public static String getLastDayOfThisYear(String year) {
        return DateUtil.format(DateUtil.offsetDay(DateUtil.parse(String.valueOf(Integer.parseInt(year) + 1), "yyyy"), -1), "yyyy-MM-dd");
    }

    /**
     * 该年最后一天结束的毫秒值
     *
     * @param year yyyy
     * @return 毫秒值
     */
    public static long getLastDayEndMillsOfThisYear(String year) {
        return DateUtil.parse((Integer.parseInt(year) + 1) + "", "yyyy").getTime() - Constant.MILLS;
    }

    /**
     * 通过毫秒值获取年月日
     *
     * @param mills 毫秒值
     * @return yyyy-MM-dd
     */
    public static String getYearAndMonthAndDay(String mills) {
        Date date = new Date();
        date.setTime(Long.parseLong(mills));
        return DateUtil.formatDate(date);
    }

    /**
     * 得到上一个星期五
     * 通过判断传过来的日期分析其是星期几，然后减去对应的天数，得到上个星期五的日期
     *
     * @param date yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static String getLastFriday(String date) {
        DateTime theDay = DateUtil.parseDate(date);
        DateTime lastDay;
        int day = DateUtil.dayOfWeek(theDay);
        if (day == 1) {
            // 周日
            lastDay = DateUtil.offsetDay(theDay, -9);
        } else if (day == 2) {
            lastDay = DateUtil.offsetDay(theDay, -3);
        } else if (day == 3) {
            lastDay = DateUtil.offsetDay(theDay, -4);
        } else if (day == 4) {
            lastDay = DateUtil.offsetDay(theDay, -5);
        } else if (day == 5) {
            lastDay = DateUtil.offsetDay(theDay, -6);
        } else if (day == 6) {
            lastDay = DateUtil.offsetDay(theDay, -7);
        } else {
            lastDay = DateUtil.offsetDay(theDay, -8);
        }
        return DateUtil.formatDate(lastDay);
    }

    /**
     * 得到某一天开始的毫秒值
     *
     * @param date yyyy-MM-dd
     * @return 毫秒值
     */
    public static String getStartMillsOfDay(String date) {
        return DateUtil.parseDate(date).getTime() + "";
    }

    /**
     * 得到某一天结束的毫秒值
     *
     * @param date yyyy-MM-dd
     * @return 毫秒值
     */
    public static String getEndMillsOfDay(String date) {
        return DateUtil.offsetDay(DateUtil.parseDate(date), 1).getTime() - Constant.MILLS + "";
    }

    /**
     * 获取到的毫秒数是否在date这天的时间内
     *
     * @param date      某一天
     * @param millsTime 毫秒值
     */
    public static Boolean betweenDate(String date, Long millsTime) {
        long beginTime = Long.parseLong(getStartMillsOfDay(date));
        long endTime = Long.parseLong(getEndMillsOfDay(date));
        return millsTime >= beginTime && millsTime <= endTime;
    }
}
