package com.example.refuelingdemo.annotaion.aspect;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.refuelingdemo.api.enums.SleepTime;
import com.example.refuelingdemo.api.repository.RedisRepository;
import com.example.refuelingdemo.common.domain.Properties;
import com.example.refuelingdemo.common.service.PropertiesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class RedisLockCheckAspect {

	private final RedisRepository redisRepository;

	private final PropertiesService propertiesService;

	@Pointcut("@annotation(com.example.refuelingdemo.annotaion.RedisLockCheck)")
	private void redisLockCheck() {
	}

	@SuppressWarnings("BusyWait")
	@Around("redisLockCheck()&&args(id,..)")
	public Object redisLockCheck(ProceedingJoinPoint joinPoint, Long id) {
		log.info("### AOP redis isLock check id:{} ###", id);
		Object returnValue = null;
		try {
			Long delay = makeDelayTime(propertiesService.findByDescription("SLEEP_200"));
			log.info("## redis spin lock delay set:{}",delay);

			while (!redisRepository.isLock(id)) {
				Thread.sleep(delay);
			}
			returnValue = joinPoint.proceed();
		} catch (Throwable e) {
			log.error(e.getMessage());
		} finally {
			redisRepository.unlock(id);
			log.info("### AOP redis unlock id:{} ###", id);
		}
		return returnValue;
	}

	private static Long makeDelayTime(Properties sleep_delay) {
		return Optional.ofNullable(sleep_delay)
			.map(Properties::getSettingValueByLong)
			.orElse(SleepTime.TIME_100.getMiles());
	}

}
