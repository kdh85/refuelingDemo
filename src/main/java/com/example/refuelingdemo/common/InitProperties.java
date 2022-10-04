package com.example.refuelingdemo.common;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.factory.PropertiesFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Profile(value = "local")
@Component
@RequiredArgsConstructor
@Slf4j
public class InitProperties {

	private final InitService initService;

	@PostConstruct
	private void init() {
		log.info("### init latency properties info ###");
		initService.createLatencyProperties(PropertyType.LATENCY);
	}

	@Component
	@RequiredArgsConstructor
	private static class InitService {

		private final PropertiesFactory propertiesFactory;

		@Transactional
		public void createLatencyProperties(PropertyType latency) {
			propertiesFactory.getPropertyType(latency)
				.createPropertiesInfo(latency);
		}
	}
}
