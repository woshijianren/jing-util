package com.exception.util;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zyl
 * @date 2020/6/19 19:56
 * @describe 该过滤器中的时间范围，凡是涉及到天的，开始的毫秒值都是当天的00:00:00，而结束的毫秒值则是根据Constant.MILLS进行了相应的减法
 * @require 根据业务的具体需求对时间进行一个毫秒值的加减，也就是describe中的当天的00:00:00的毫秒值是否需要修改
 */
@Slf4j
public class FilterUtils {

    // ============================下方的过滤器都是有上边界和下边界的，在这个时间段内============================

    /**
     * 毫秒时间范围过滤器
     * 获取在者两个毫秒时间范围段的数据
     *
     * @param fieldName    字段名
     * @param startMills   开始时间：毫秒
     * @param endTimeMills 结束时间：毫秒
     */
    public static Filter timeFilter(String fieldName, String startMills, String endTimeMills) {
        Filter timeFilter = new Filter();
        List<String> timeList = new ArrayList<>();
        timeList.add(startMills);
        timeList.add(endTimeMills);
        timeFilter.setFieldName(fieldName);
        timeFilter.setOperator(Operator.BETWEEN);
        timeFilter.setFieldValues(timeList);
        return timeFilter;
    }

    /**
     * 年过滤器
     * 在一整年的时间范围内的数据
     * 例子：year = 2020 , 取2020-1-1 00:00:00至 2020-12-31 23：59：59的毫秒值
     *
     * @param year 例：2020
     */
    public static Filter yearFilter(String fieldName, String year) {
        List<String> yearMills = new ArrayList<>();
        // 该年年度开始时的毫秒
        Long thisYearBeginMills = DateUtil.parse((Integer.parseInt(year)) + "", "yyyy").getTime();
        log.info(String.valueOf(thisYearBeginMills));
        // 该年年度结束时的毫秒, 晚23：59：59，所以减1000毫秒
        Long yearEndMills = DateUtil.parse((Integer.parseInt(year) + 1) + "", "yyyy").getTime() - Constant.MILLS;
        log.info(String.valueOf(yearEndMills));
        yearMills.add(thisYearBeginMills.toString());
        yearMills.add(yearEndMills.toString());
        return new Filter(fieldName, Operator.BETWEEN, yearMills);
    }

    /**
     * 非空过滤器
     *
     * @param fieldName 要求该字段的值不为null
     */
    public static Filter notNullFilter(String fieldName) {
        return new Filter(fieldName, Operator.ISN, null);
    }

    /**
     * 组合所有数据器
     * 因为请求数据的方法，需要传入的参数是List<Filter>，
     * 而每次add Filter 进入List太过繁琐
     *
     * @param filters 单个过滤器
     */
    public static List<Filter> composeFilter(Filter... filters) {
        List<Filter> filtersList = new ArrayList<>();
        Collections.addAll(filtersList, filters);
        return filtersList;
    }

    /**
     * 最普通的过滤器，主要用来简化new Filter时需要创建List
     */
    public static Filter commonFilter(String fieldName, Operator operator, String... values) {
        List<String> stringList = new ArrayList<>();
        stringList.addAll(Arrays.asList(values));
        return new Filter(fieldName, operator, stringList);
    }

    /**
     * 日过滤器，一整天
     *
     * @param date 例：2020-8-3
     */
    public static Filter dayFilter(String fieldName, String date) {
        date = TimeUtil.getDay(date);
        long beginMills = DateUtil.parseDate(date).getTime();
        long endMills = DateUtil.offsetDay(DateUtil.parseDate(date), 1).getTime() - Constant.MILLS;
        return commonFilter(fieldName, Operator.BETWEEN, beginMills + "", endMills + "");
    }

    /**
     * 日过滤器，多天
     *
     * @param fieldName 字段名
     * @param beginDate 开始日期 例：2020-8-3
     * @param endDate   结束日期 例：2020-9-10
     */
    public static Filter dayFilter(String fieldName, String beginDate, String endDate) {
        String beginMills = TimeUtil.getStartMillsOfDay(beginDate);
        String endMills = TimeUtil.getEndMillsOfDay(endDate);
        return commonFilter(fieldName, Operator.BETWEEN, beginMills, endMills);
    }

    /**
     * 月过滤器，整月
     * 开始时间为该月1号00：00：00毫秒，结束为该月最后一日23：59：59
     *
     * @param month yyyy-MM
     */
    public static Filter monthFilter(String fieldName, String month) {
        String format = "yyyy-MM";
        month = TimeUtil.getMonth(month, format);
        long begin = DateUtil.parse(month, format).getTime();
        long end = DateUtil.parse(DateUtil.format(DateUtil.offsetMonth(DateUtil.parse(month, format), 1), format), new SimpleDateFormat(format)).getTime() - Constant.MILLS;
        return commonFilter(fieldName, Operator.BETWEEN, begin + "", end + "");
    }

    /**
     * 季度过滤器
     * 从该季度开始的第一天的00:00:00到该季度结束的最后一天的最后一个毫秒
     *
     * @param fieldName 字段名
     * @param year      年份，例：2020
     * @param quarter   季度：可选值为1，2，3，4，只能为这4个数字
     * @return
     */
    public static Filter quarterFilter(String fieldName, String year, String quarter) {
        year = TimeUtil.getYear(year);
        quarter = TimeUtil.getQuarter(quarter);
        String startMonth, endMonth;
        if ("1".equals(quarter)) {
            startMonth = year + "-" + "01";
            endMonth = year + "-" + "03";
        } else if ("2".equals(quarter)) {
            startMonth = year + "-" + "04";
            endMonth = year + "-" + "06";
        } else if ("3".equals(quarter)) {
            startMonth = year + "-" + "07";
            endMonth = year + "-" + "09";
        } else {
            startMonth = year + "-" + "10";
            endMonth = year + "-" + "12";
        }
        String format = "yyyy-MM";
        long begin = DateUtil.parse(startMonth, format).getTime();
        long end = DateUtil.parse(DateUtil.format(DateUtil.offsetMonth(DateUtil.parse(endMonth, format), 1), format), new SimpleDateFormat(format)).getTime() - Constant.MILLS;
        return commonFilter(fieldName, Operator.BETWEEN, begin + "", end + "");
    }


    // ============================下方的过滤器只有下边界而没有上边界============================

    /**
     * 到选中年结束，包含选中年，即到选中年的最后一天的最后毫秒值
     *
     * @param year yyyy
     */
    public static Filter asOfYearEndFilter(String fieldName, String year) {
        List<String> yearMills = new ArrayList<>();
        // 该年年度结束时的毫秒, 晚23：59：59，所以减1000毫秒
        Long yearEndMills = DateUtil.parse((Integer.parseInt(year) + 1) + "", "yyyy").getTime() - Constant.MILLS;
        yearMills.add(yearEndMills.toString());
        return new Filter(fieldName, Operator.LTE, yearMills);
    }

    /**
     * 到选中年的选中季度的时间结束的最后毫秒值，包含选中年的选中季度，即下个季度之前
     *
     * @param year    yyyy
     * @param quarter 可用值：1,2,3,4
     */
    public static Filter asOfYearAndQuarterEndFilter(String fieldName, String year, String quarter) {
        year = TimeUtil.getYear(year);
        quarter = TimeUtil.getQuarter(quarter);
        String endMonth;
        if ("1".equals(quarter)) {
            endMonth = year + "-" + "03";
        } else if ("2".equals(quarter)) {
            endMonth = year + "-" + "06";
        } else if ("3".equals(quarter)) {
            endMonth = year + "-" + "09";
        } else {
            endMonth = year + "-" + "12";
        }
        String format = "yyyy-MM";
        long end = DateUtil.parse(DateUtil.format(DateUtil.offsetMonth(DateUtil.parse(endMonth, format), 1), format), new SimpleDateFormat(format)).getTime() - Constant.MILLS;
        return commonFilter(fieldName, Operator.LTE, end + "");
    }


    public static void main(String[] args) {
        String date = "2020-7-3";

        long beginMills = DateUtil.parseDate(date).getTime();
        log.info(beginMills + "");
        long endMills = DateUtil.offsetDay(DateUtil.parseDate(date), 1).getTime() - Constant.MILLS;
        log.info(endMills + "");
    }

}
