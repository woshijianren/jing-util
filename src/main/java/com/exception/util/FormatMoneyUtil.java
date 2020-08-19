package com.exception.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class FormatMoneyUtil {


    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

    /**
     * 对金额格式化: 除以unit后按照scale进行小数位数的四舍五入然后转为三位分节计数法
     *
     * @param money 金额
     * @param unit  单位：一般是UNIT_NUMBER(10000.00)
     * @param scale 四舍五入的位数
     * @return String
     */
    public static String formatMoneyToString(BigDecimal money, BigDecimal unit, Integer scale) {
        if (unit != null) {
            return FormatMoneyUtil.fmtMicrometer(money.divide(unit).setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString());
        } else {
            return FormatMoneyUtil.fmtMicrometer(money.setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString());
        }
    }

//    public static String defaultFormatMoney(BigDecimal money) {
//        return formatMoneyToString(money, Constant.UNIT, Constant.SCALE);
//    }

    public static String noUnitFormatMoney(BigDecimal money) {
        return formatMoneyToString(money, null, Constant.SCALE);
    }
}
