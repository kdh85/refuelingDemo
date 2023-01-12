package com.example.refuelingdemo.annotaion.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class RedissonLockCheckAspect {

    private final RedissonClient redissonClient;

    @Pointcut("@annotation(com.example.refuelingdemo.annotaion.RedissonLockCheck)")
    private void redissonLockCheck() {
    }

    @Around("redissonLockCheck()&&args(id,..)")
    public Object redissonLockCheck(ProceedingJoinPoint joinPoint, Long id) {

        log.info("###Thread :{} / AOP redisson isLock check id:{} ###", Thread.currentThread().getName(), id);
        Object returnValue = null;
        RLock lock = redissonClient.getLock(String.valueOf(id));
        try {
            if (lock.tryLock(1000, 500, TimeUnit.MILLISECONDS)) {
                returnValue = joinPoint.proceed();
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return returnValue;
    }
}
