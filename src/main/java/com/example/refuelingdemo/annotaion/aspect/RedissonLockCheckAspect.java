package com.example.refuelingdemo.annotaion.aspect;

import static com.example.refuelingdemo.common.enums.DelayType.*;
import static com.example.refuelingdemo.common.enums.PropertyType.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.example.refuelingdemo.api.enums.SleepTime;
import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.service.PropertiesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class RedissonLockCheckAspect {

	private final RedissonClient redissonClient;

	private final PropertiesService propertiesService;

	@Pointcut("@annotation(com.example.refuelingdemo.annotaion.RedissonLockCheck)")
	private void redissonLockCheck() {
	}

	@Around("redissonLockCheck()&&args(id,..)")
	public Object redissonLockCheck(ProceedingJoinPoint joinPoint, Long id) {

		log.info("###Thread :{} / AOP redisson isLock check id:{} ###", Thread.currentThread().getName(), id);
		RLock lock = redissonClient.getLock(String.valueOf(id));
		Object returnValue = null;
		try {

			List<ResponsePropertiesDto> allChildByParentType = propertiesService.findAllChildByParentType(
				LATENCY);
			log.info("##### allChildByParentType :{}", allChildByParentType);
			Long lockDelay = allChildByParentType.stream()
				.filter(p -> p.getPropertyType() == TRY_LOCK_DELAY)
				.map(ResponsePropertiesDto::getSettingValueByLong)
				.findFirst()
				.orElse(SleepTime.TIME_3000.getMiles());

			log.info("## redisson lock delay set:{}", lockDelay);

			while (lock.tryLock(0, lockDelay, TimeUnit.MILLISECONDS)) {
				returnValue = joinPoint.proceed();
			}

		} catch (Throwable e) {
			log.error(e.getMessage());
		} finally {
			log.info("### AOP redisson unlock id:{} ###", id);
			lock.unlock();
		}
		return returnValue;
	}
}
