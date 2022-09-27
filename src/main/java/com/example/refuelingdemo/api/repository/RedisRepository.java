package com.example.refuelingdemo.api.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.refuelingdemo.api.enums.SleepTime;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisRepository {

	private static final String LOCK = "lock";

	private final RedisTemplate<String, String> redisTemplate;

	public Boolean isLock(final Long key) {
		return redisTemplate.opsForValue()
			.setIfAbsent(key.toString(), LOCK, Duration.ofMillis(SleepTime.TIME_3000.getMiles()));
	}

	public void unlock(final Long key) {
		redisTemplate.delete(key.toString());
	}
}
