package com.qy.ticket.aspect;

import com.qy.ticket.annotation.RecordLock;
import com.qy.ticket.constant.RedisConstant;
import com.qy.ticket.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.qy.ticket.constant.RedisConstant.KEY_RECORD_LOCK;
import static com.qy.ticket.exception.EumException.SYSTEM_BUSY;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午9:45
 **/
@Slf4j
@Aspect
@Order(4)
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecordLockAop {
    private final RedissonClient redissonSingle;

    @Pointcut(value = "execution(public * com.qy.ticket.service.impl..*.*(..))")
    public void recordPointcut() {
    }

    @Around("recordPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();
        Annotation userAnnotation = method.getAnnotation(RecordLock.class);
        if (null != userAnnotation) {
            return dealOrderLock(point, args);
        }
        return point.proceed(args);
    }

    /**
     * 处理UserLock
     *
     * @param point
     * @param args
     * @return
     * @throws Throwable
     */
    private Object dealOrderLock(ProceedingJoinPoint point, Object[] args) throws Throwable {
        RLock lock = redissonSingle.getLock(RedisConstant.concat(KEY_RECORD_LOCK, args[0].toString()));
        try {
            // 5秒获取不到锁则提示,最多持有锁60s
            if (lock.tryLock(5L, 60L, TimeUnit.SECONDS)) {
                return point.proceed(args);
            } else {
                throw new BusinessException(SYSTEM_BUSY);
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
