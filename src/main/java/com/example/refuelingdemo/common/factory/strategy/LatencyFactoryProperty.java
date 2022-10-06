package com.example.refuelingdemo.common.factory.strategy;

import static com.example.refuelingdemo.common.enums.PropertyType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.refuelingdemo.common.domain.ConfigProperties;
import com.example.refuelingdemo.common.enums.DelayType;
import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.service.PropertiesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LatencyFactoryProperty implements FactoryProperty {

	private static final String subFix = "_ms";
	private final PropertiesService propertiesService;

	@Override
	public void createPropertiesInfo(PropertyType propertyType) {

		ConfigProperties parentConfigProperties = propertiesService.createParentProperties(propertyType,
			propertyType.getDescription(), null);

		List<ConfigProperties> childProperties = new ArrayList<>();
		for (DelayType delayType : findByPropertyType(propertyType)) {
			childProperties.add(ConfigProperties.createChildProperties(delayType, delayType.name(),
				delayType.getSleepTime().getMilesToString(), parentConfigProperties));
		}

		log.info("### children properties :{}", childProperties);
		propertiesService.createBulkChildProperties(childProperties);
	}

	@Override
	public void createPropertiesBulkInfo(PropertyType propertyType) {

		List<String> defaultDelay = List.of("100", "150", "200", "3000");

		ConfigProperties parentConfigProperties = propertiesService.createParentProperties(propertyType,
			propertyType.getDescription(), null);

		List<ConfigProperties> children = defaultDelay.stream()
			.map(delay -> ConfigProperties.createChildProperties(propertyType, generateDescription(delay), delay,
				parentConfigProperties))
			.collect(Collectors.toList());

		log.info("### children properties :{}", children);
		propertiesService.createBulkChildProperties(children);
	}

	private static String generateDescription(final String delay) {
		return delay + subFix;
	}
}
