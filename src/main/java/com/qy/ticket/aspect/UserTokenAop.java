package com.qy.ticket.aspect;

import com.qy.ticket.annotation.IgnoreUserToken;
import com.qy.ticket.common.CommonResult;
import com.qy.ticket.config.ValidatorImpl;
import com.qy.ticket.constant.RedisConstant;
import com.qy.ticket.context.BaseContext;
import com.qy.ticket.exception.BusinessException;
import com.qy.ticket.exception.EumException;
import com.qy.ticket.util.JwtUtil;
import com.qy.ticket.util.ValidationResult;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.qy.ticket.constant.SystemConstant.*;
import static com.qy.ticket.exception.EumException.SERVICE_ERROR;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午1:36
 **/
@Slf4j
@Aspect
@Order(2)
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserTokenAop {
    private final HttpServletRequest request;
    private final RedissonClient redissonSingle;
    private final ValidatorImpl validator;
    private final JwtUtil jwtOperator;

    @Pointcut(value = "execution(public * com.qy.ticket.controller..*.*(..))")
    public void commonPointcut() {
    }

    @Around("commonPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();
        Annotation authAnnotation = method.getAnnotation(IgnoreUserToken.class);

        // token处理
        if (null == authAnnotation) {
            String wthToken = request.getHeader("QYToken");
            if (StringUtils.isEmpty(wthToken)) {
                return new CommonResult(EumException.NOT_LOGIN);
            }
            buildBaseContext(wthToken);
            RBucket<String> bucket = redissonSingle.getBucket(RedisConstant.concat(RedisConstant.KEY_USER_TOKEN, BaseContext.getUserId().toString()));
            if (!bucket.isExists()) {
                return new CommonResult(EumException.NOT_LOGIN);
            }
        }

        // 校验入参
        if (!ObjectUtils.isEmpty(args) && !request.getMethod().equals("GET")) {
            for (int i = 0; i < args.length; i++) {
                ValidationResult result = validator.validate(args[i]);
                if (result.isHasErrors()) {
                    BusinessException businessException = new BusinessException(EumException.PERMISSION_ERROR);
                    businessException.setMsg(result.getErrMsg());
                    throw businessException;
                }
            }
        }

        // 业务执行
        try {
            return point.proceed(args);
        } catch (BusinessException be) {
            return new CommonResult(be);
        } catch (Exception e) {
            log.error("服务异常", e);
            return new CommonResult(SERVICE_ERROR);
        }
    }

    /**
     * 获取目标主机的ip
     *
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 构建BaseContext
     */
    private void buildBaseContext(String wthToken) {
        Claims claimsFromToken = jwtOperator.getClaimsFromToken(wthToken);
        String userId = claimsFromToken.get(CONTEXT_KEY_USER_ID) + "";
        String name = (String) claimsFromToken.get(CONTEXT_KEY_USER_NAME);
        String phoneNum = (String) claimsFromToken.get(CONTEXT_KEY_USER_PHONE);
        String openId = (String) claimsFromToken.get(CONTEXT_KEY_USER_OPEN_ID);
        String ipAddr = getRemoteHost(request);

        BaseContext.setUserId(Long.parseLong(userId));
        BaseContext.setUserIp(ipAddr);
        BaseContext.setUserName(name);
        BaseContext.setUserPhoneNum(phoneNum);
        BaseContext.setUserOpenId(openId);
    }
}
