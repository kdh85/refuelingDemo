package com.example.refuelingdemo.annotaion.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.refuelingdemo.api.enums.SleepTime;
import com.example.refuelingdemo.api.repository.RedisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class RedisLockCheckAspect {

	private final RedisRepository redisRepository;

	@Pointcut("@annotation(com.example.refuelingdemo.annotaion.RedisLockCheck)")
	private void redisLockCheck(){}

	@Around("redisLockCheck()&&args(id,..)")
	public Object redisLockCheck(ProceedingJoinPoint joinPoint, Long id) {
		log.info("### AOP redis isLock check id:{} ###",id);
		Object returnValue = null;
		try {

			while (!redisRepository.isLock(id)) {
				Thread.sleep(SleepTime.TIME_100.getMiles());
			}
			returnValue = joinPoint.proceed();
		} catch (Throwable e) {
			log.error(e.getMessage());
		} finally {
			redisRepository.unlock(id);
			log.info("### AOP redis unlock id:{} ###",id);
		}
		return returnValue;
	}

}
