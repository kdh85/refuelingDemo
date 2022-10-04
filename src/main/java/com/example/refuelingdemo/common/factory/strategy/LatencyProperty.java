package com.example.refuelingdemo.common.factory.strategy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.refuelingdemo.common.domain.Properties;
import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.service.PropertiesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LatencyProperty implements Property {

	private static final String subFix = "_ms";
	private final PropertiesService propertiesService;

	@Override
	public void createPropertiesInfo(PropertyType latency) {
		List<String> defaultDelay = List.of("100", "150", "200", "3000");

		Properties parentProperties = propertiesService.createParentProperties(latency, latency.getDescription(), null);

		List<Properties> children = defaultDelay.stream()
			.map(delay -> Properties.createChildProperties(latency, generateDescription(delay), delay,
				parentProperties))
			.collect(Collectors.toList());

		log.info("### children properties :{}", children);
		propertiesService.createBulkChildProperties(children);
	}

	private static String generateDescription(final String delay) {
		return delay+subFix;
	}
}
