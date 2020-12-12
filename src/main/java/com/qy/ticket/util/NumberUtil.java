package com.qy.ticket.util;

import java.math.BigDecimal;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午4:23
 **/
public class NumberUtil {
    /**
     * 分转化为元
     *
     * @param b1
     * @return
     */
    public static String divide100(BigDecimal b1) {
        return b1.movePointLeft(2).toString();
    }

    /**
     * 元转化为分
     *
     * @param b1
     * @return
     */
    public static Integer multiply100(BigDecimal b1) {
        return b1.movePointRight(2).intValue();
    }

    /**
     * 四舍五入
     *
     * @param a1
     * @param a2
     * @return
     */
    public static int multiplyHalfUp(double a1, double a2) {
        return multiply(a1, a2, BigDecimal.ROUND_HALF_UP);
    }

    public static int multiply(double a1, double a2, int type) {
        BigDecimal b1 = new BigDecimal(a1);
        BigDecimal b2 = new BigDecimal(a2);
        return b1.multiply(b2).setScale(0, type).intValue();
    }
}
