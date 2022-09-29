package com.example.refuelingdemo.annotaion.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ExecutionTimer {

	@Pointcut("@annotation(com.example.refuelingdemo.annotaion.ExeTimer)")
	private void timer(){};

	// 메서드 실행 전,후로 시간을 공유해야 하기 때문
	@Around("timer()")
	public Object AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		Object proceed = joinPoint.proceed();// 조인포인트의 메서드 실행
		stopWatch.stop();

		long totalTimeMillis = stopWatch.getTotalTimeMillis();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String methodName = signature.getMethod().getName();

		log.info("실행 메서드: {}, 실행시간 = {}ms", methodName, totalTimeMillis);
		return proceed;
	}
}
