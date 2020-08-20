package com.util;

import java.math.BigDecimal;

/**
 * @author zyl
 * @date 2020/2/27
 */
public class Constant {

    // 历史时间，最早是哪一月. 格式：yyyy-MM，     注：如果此处格式改变，需要在调用方修改格式化时间的format
    public final static String HISTORY_MONTH = "2020-01";

    // 历史时间，最早是哪一天. 格式：yyyy-MM-dd，     注：如果此处格式改变，需要在调用方修改格式化时间的format
    public final static String HISTORY_DAY = "2020-01-01";

    // 按天过滤时间时，离24:00:00多少秒就算一整天
    public final static Long MILLS = 1L;

    // 三位分节法：保留两位小数
    public final static Integer SCALE = 2;

    // 对于空值的处理
    public final static String NULL_STRING = null;

    public final static BigDecimal NULL_BIG_DECIMAL = null;

}
