package com.example.refuelingdemo.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Profile("local")
@Configuration
@Slf4j
public class EmbeddedRedisConfig {

	@Value("${spring.redis.port}")
	private int port;

	private RedisServer redisServer;

	@PostConstruct
	@DependsOn("redissonClient")
	public void redisServerStart() {
		redisServer = new RedisServer();
		redisServer.start();
		log.info("#### embedded redis start  port :{} #######",port);
	}

	@PreDestroy
	private void redisServerStop() {
		redisServer.stop();
		log.info("#### embedded redis stop  port :{} #######",port);
	}

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://localhost:6379");
		return Redisson.create(config);
	}
}
