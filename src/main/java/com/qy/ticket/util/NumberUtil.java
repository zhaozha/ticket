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



}
