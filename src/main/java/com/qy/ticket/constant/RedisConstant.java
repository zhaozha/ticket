package com.qy.ticket.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/18 下午8:46
 **/
public class RedisConstant {
    public static final String COLON = ":";
    public static final String MATCH_ALL = COLON + "*";
    // 用户token
    public static final String KEY_USER_TOKEN = "ticket2020:user:token";
    // 管理员token
    public static final String KEY_MANAGER_TOKEN = "ticket2020:manager:token";
    // 全局锁(保证用户单线程操作指定资源)
    public static final String KEY_USER_DISTRIBUTED_LOCK = "ticket2020:user:distribute:lock";
    // 行程锁
    public static final String KEY_RECORD_LOCK = "ticket2020:user:record:lock";
    // 当日票号步长
    public static final String KEY_TICKET_SEQ = "ticket2020:user:ticker:seq";


    public static String matchAll(String key) {
        return key + MATCH_ALL;
    }

    public static String concat(String... keys) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            if (!StringUtils.isEmpty(keys[i])) {
                builder.append(keys[i]);
                if (i < keys.length - 1) {
                    builder.append(COLON);
                }
            }
        }
        return builder.toString();
    }
}