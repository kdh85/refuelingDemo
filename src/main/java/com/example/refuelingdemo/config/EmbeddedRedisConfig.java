package com.example.refuelingdemo.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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
	public void redisServerStart() {
		redisServer = new RedisServer(port);
		redisServer.start();
		log.info("#### embedded redis start  port :{} #######",port);
	}

	@PreDestroy
	private void redisServerStop() {
		redisServer.stop();
		log.info("#### embedded redis stop  port :{} #######",port);
	}

}
