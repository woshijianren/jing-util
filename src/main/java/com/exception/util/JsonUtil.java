package com.exception.util;

import cn.hutool.core.date.DateUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zyl
 * @date 2020/6/18 15:54
 * @desribe 凡是方法返回值用了Constant中的常量的，都是需要根据具体的业务需求来处理空值的
 * 因此，凡是对该方法的返回值进行判断，一律要求使用Constant中的常量，不得使用魔法值
 */
@Slf4j
public class JsonUtil {

    // 下列所有方法都是默认JsonObject不为null

    /**
     * 根据apiName获取BigDecimal的值
     *
     * @return 是空值就返回0
     */
    public static BigDecimal getJsonBigDecimal(JsonObject datum, String apiName) {
        if (datum.get(apiName).isJsonNull() || "".equals(datum.get(apiName).getAsString())) {
            return new BigDecimal("0.00");
        }
        return datum.get(apiName).getAsBigDecimal();
    }

    /**
     * 获取的值不进行计算时使用
     * 根据apiName获取BigDecimal的值
     * @return 但是如果是空，我们则返回Constant.NULL_BIG_DECIMAL
     */
    private static BigDecimal getJsonBigDecimalNull(JsonObject datum, String apiName) {
        if (datum.get(apiName).isJsonNull() || "".equals(datum.get(apiName).getAsString())) {
            return Constant.NULL_BIG_DECIMAL;
        }
        return datum.get(apiName).getAsBigDecimal();
    }

    /**
     * 处理JsonObject类型的金额
     * 以其中的“equivalentMoney”为准（注：乘以了汇率之后的结果，货币代码为CNY）
     *
     * @return 空值返回0
     */
    public static BigDecimal getBigDecimalFromJsonObject(JsonObject datum, String apiName) {
        if (!datum.get(apiName).isJsonNull()) {
            return datum.get(apiName).getAsJsonObject().get("equivalentMoney").getAsBigDecimal();
        }
        return new BigDecimal("0.00");
    }

    /**
     * 一般用于求取人数，获取时间（ms）
     *
     * @return 空值返回0
     */
    public static Long getJsonLong(JsonObject datum, String apiName) {
        if (datum.get(apiName).isJsonNull() || "".equals(datum.get(apiName).getAsString())) {
            return 0L;
        }
        return datum.get(apiName).getAsLong();
    }

    /**
     * 根据apiName获取字符串结果
     *
     * @return 如果是空值，最后用Constant.NULL_STRING返回
     */
    public static String getJsonString(JsonObject datum, String apiName) {
        if (datum.get(apiName).isJsonNull() || "".equals(datum.get(apiName).getAsString())) {
            return Constant.NULL_STRING;
        }
        return datum.get(apiName).getAsString();
    }

    /**
     * JsonArray中只有一个JsonObject，并且只需要获取JsonObject中具体某个字段的值
     *
     * @param datum   JsonArray
     * @param apiName 返回值为JsonObject，
     * @param field   获取JsonObject中改字段的值
     * @return 如果是空，返回Constant.STRING_NULL
     */
    public static String getJsonArrayFirstString(JsonObject datum, String apiName, String field) {
        if (datum.get(apiName).isJsonNull()) {
            return Constant.NULL_STRING;
        }
        return datum.getAsJsonArray(apiName).get(0).getAsJsonObject().get(field).getAsString();
    }

    /**
     * 将日期转成yyyyMMdd的结果
     *
     * @return 如果不是Constant.STRING_NULL则转换为yyyyMMdd的类型
     */
    public static String getJsonDate(JsonObject datum, String apiName) {
        String s = getJsonString(datum, apiName);
        if (Constant.NULL_STRING != s) {
            Date date = new Date();
            date.setTime(Long.parseLong(s));
            return DateUtil.format(date, "yyyyMMdd");
        }
        return s;
    }


    // ==========================================================================
    // 下列方法禁止使用，下次更新将删除，因为违法了单一原则，过多的处理导致后期需求修改时不灵活
    // 当初使用下列方法有遇到大坑，因此弃用，下次更新时删除











    /**
     * 三位分节法的结果
     *
     * @param datum
     * @param apiName
     * @return String
     */
    public static String getThreePart(JsonObject datum, String apiName) {
        BigDecimal money = getJsonBigDecimalNull(datum, apiName);
        if (money != Constant.NULL_BIG_DECIMAL) {
            return FormatMoneyUtil.noUnitFormatMoney(money);
        }
        return Constant.NULL_STRING;
    }

    /**
     * 截取“1-私募证券投资基金”中的1，规律：只有一个“-”连接符，取前面一部分，无论getTableData数据的结果是“1”还是“1-私募证券投资基金”
     *
     * @param datum
     * @param apiName
     * @param firstIndexString 一般是"-"
     * @return
     */
    public static String interceptString(JsonObject datum, String apiName, String firstIndexString) {
        String s = getJsonString(datum, apiName);
        if (Constant.NULL_STRING != s) {
            if (s.indexOf(firstIndexString) > 0) {
                return s.substring(0, s.indexOf(firstIndexString));
            }
            return s;
        }
        return Constant.NULL_STRING;
    }

    /**
     * 合并，直投基金和FOF基金的产品代码二选一
     *
     * @param datum
     * @param apiName        直投|FOF
     * @param anotherOpiName 直投|FOF
     * @param fieldName      name
     * @return
     */
    public static String mergeProductCode(JsonObject datum, String apiName, String anotherOpiName, String fieldName) {
        String s = getJsonArrayFirstString(datum, apiName, fieldName);
        if (s == Constant.NULL_STRING) {
            s = getJsonArrayFirstString(datum, anotherOpiName, fieldName);
        }
        return s;
    }

    /**
     * 有一种特殊的类型——
     *
     * @param datum
     * @param apiName
     * @param fieldName
     * @return
     */
    public static String getThreePartFromJsonObject(JsonObject datum, String apiName, String fieldName) {
        if (!datum.get(apiName).isJsonNull()) {
            BigDecimal money = datum.get(apiName).getAsJsonObject().get(fieldName).getAsBigDecimal();
            return FormatMoneyUtil.noUnitFormatMoney(money);
        }
        return Constant.NULL_STRING;
    }

    /**
     * 合并其他列
     * shmilys
     *
     * @return
     */
    public static String mergeColumns(JsonObject datum, String apiName, String anotherOpiName) {
        String s = getJsonString(datum, apiName);
        if (s == Constant.NULL_STRING) {
            s = getJsonString(datum, anotherOpiName);
        }
        return s;
    }

    /**
     * 处理对象JsonObject类型的金额
     */
    public static String getDisposeSpecialMoneyJsonObject(JsonObject datum, String apiName) {
        if (!datum.get(apiName).isJsonNull()) {
            BigDecimal money = datum.get(apiName).getAsJsonObject().get("equivalentMoney").getAsBigDecimal();
            return FormatMoneyUtil.noUnitFormatMoney(money);
        }
        return Constant.NULL_STRING;
    }


    /**
     * 用于判断一个Integer是否为空
     *
     * @param value
     * @return
     */
    public static boolean isNullInteger(Integer value) {
        if (value != null) {
            return true;
        }
        return false;
    }

}
