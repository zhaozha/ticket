package com.qy.ticket.context;
import java.util.HashMap;
import java.util.Map;

import static com.qy.ticket.constant.SystemConstant.*;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/18 下午9:27
 **/
public class BaseContext {

    public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        return map.get(key);
    }

    /**
     * @return 用户id
     */
    public static Object getUserId() {
        Object value = get(CONTEXT_KEY_USER_ID);
        return returnObjectValue(value);
    }

    /**
     * @return 用户名称
     */
    public static Object getUserName() {
        Object value = get(CONTEXT_KEY_USER_NAME);
        return returnObjectValue(value);
    }

    /**
     * @return 用户登录IP
     */
    public static Object getUserIp() {
        Object value = get(CONTEXT_KEY_USER_IP);
        return returnObjectValue(value);
    }

    /**
     * @return 用户手机号
     */
    public static Object getUserPhoneNum() {
        Object value = get(CONTEXT_KEY_USER_PHONE);
        return returnObjectValue(value);
    }

    /**
     * @return 用户openId
     */
    public static Object getUserOpenId() {
        Object value = get(CONTEXT_KEY_USER_OPEN_ID);
        return returnObjectValue(value);
    }

    /**
     * @return 代理标记
     */
    public static Object getUserAgentType() {
        Object value = get(CONTEXT_KEY_USER_AGENT_TYPE);
        return returnObjectValue(value);
    }

    public static void setUserId(Long userId) {
        set(CONTEXT_KEY_USER_ID, userId);
    }

    public static void setUserName(String userName) {
        set(CONTEXT_KEY_USER_NAME, userName);
    }

    public static void setUserIp(String userIp) {
        set(CONTEXT_KEY_USER_IP, userIp);
    }

    public static void setUserPhoneNum(String phoneNum) {
        set(CONTEXT_KEY_USER_PHONE, phoneNum);
    }

    public static void setUserOpenId(String openId) {
        set(CONTEXT_KEY_USER_OPEN_ID, openId);
    }

    public static void setUserAgentType(Integer agentType) {
        set(CONTEXT_KEY_USER_AGENT_TYPE, agentType);
    }

    private static Object returnObjectValue(Object value) {
        return value;
    }

    public static void remove() {
        threadLocal.remove();
    }
}

