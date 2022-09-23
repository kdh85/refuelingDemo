package com.example.refuelingdemo.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import redis.embedded.RedisServer;

@Profile("local")
@Configuration
public class EmbeddedRedisConfig {

	@Value("${spring.redis.port}")
	private int port;

	private RedisServer redisServer;

	@PostConstruct
	public void redisServerStart() {
		redisServer = new RedisServer(port);
		redisServer.start();
	}

	@PreDestroy
	private void redisServerStop() {
		redisServer.stop();
	}

}
