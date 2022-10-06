package com.example.refuelingdemo.annotaion.aspect;

import static com.example.refuelingdemo.common.enums.PropertyType.*;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.refuelingdemo.api.enums.SleepTime;
import com.example.refuelingdemo.api.repository.RedisRepository;
import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.enums.DelayType;
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

			List<ResponsePropertiesDto> allChildByParentType = propertiesService.findAllChildByParentType(LATENCY);

			Long lockDelay = allChildByParentType.stream()
				.filter(p -> p.getPropertyType() == DelayType.LOCK_DELAY)
				.map(ResponsePropertiesDto::getSettingValueByLong)
				.findFirst()
				.orElse(SleepTime.TIME_3000.getMiles());

			Long spinDelay = allChildByParentType.stream()
				.filter(p -> p.getPropertyType() == DelayType.SPIN_LOCK_DELAY)
				.map(ResponsePropertiesDto::getSettingValueByLong)
				.findFirst()
				.orElse(SleepTime.TIME_200.getMiles());

			log.info("## redis lock delay set:{}",lockDelay);
			log.info("## redis spin lock delay set:{}",spinDelay);

			while (!redisRepository.isLock(id, lockDelay)) {
				Thread.sleep(spinDelay);
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
}
