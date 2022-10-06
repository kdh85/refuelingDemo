package com.example.refuelingdemo.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.common.domain.ConfigProperties;
import com.example.refuelingdemo.common.enums.PropertyType;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ConfigPropertiesServiceTest {

	@Autowired
	private PropertiesService propertiesService;

	@DisplayName("부모 설정값 생성 테스트.")
	@Test
	@Transactional
	void createParentTest() {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		String parentDesc = "latency";
		String parentSetValue = "latencyValue";
		//when
		ConfigProperties parentConfigProperties = propertiesService.createParentProperties(propertyType, parentDesc,
			parentSetValue);
		//then
		assertAll(
			() -> assertEquals(parentConfigProperties.getPropertyType(), propertyType),
			() -> assertEquals(parentConfigProperties.getDescription(), parentDesc),
			() -> assertEquals(parentConfigProperties.getSettingValue(), parentSetValue)
		);
	}

	@DisplayName("부모 및 자식 설정값 생성 테스트.")
	@Test
	@Transactional
	void createParentAndChildTest() {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		String parentDesc = "latency";
		String parentSetValue = "latencyValue";
		String childDesc = "sleep_3000";
		String childSetValue = "3000";

		//when
		ConfigProperties parentConfigProperties = propertiesService.createParentProperties(propertyType, parentDesc,
			parentSetValue);
		propertiesService.createChildProperties(propertyType, childDesc, childSetValue, parentConfigProperties);

		//then
		ConfigProperties findChild = propertiesService.findByDescription(childDesc);
		log.info("findChild :{}", findChild);

		assertAll(
			() -> assertEquals(findChild.getParent().getDescription(), parentDesc),
			() -> assertEquals(findChild.getParent().getSettingValue(), parentSetValue),
			() -> assertEquals(findChild.getDescription(), childDesc),
			() -> assertEquals(findChild.getSettingValue(), childSetValue),
			() -> assertEquals(findChild.getSettingValueByLong(), Long.parseLong(childSetValue))
		);
	}

	@DisplayName("부모없이 자식 설정값 생성시 에러 반환 테스트.")
	@Test
	@Transactional
	void createChildFailTest() {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		String childDesc = "sleep_3000";
		String childSetValue = "3000";

		//then
		assertThatThrownBy(
			() -> propertiesService.createChildProperties(propertyType, childDesc, childSetValue, null)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("N개의 자식 설정값 등록 테스트.")
	@Test
	void createChildBulkSTest() {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		String parentDesc = "latency";
		String parentSetValue = "latencyValue";
		String childDesc = "sleep";
		List<String> childrenSetValue = List.of("100", "150", "200", "3000");

		//when
		ConfigProperties parentConfigProperties = propertiesService.createParentProperties(propertyType, parentDesc,
			parentSetValue);

		List<ConfigProperties> children = childrenSetValue.stream()
			.map(val -> ConfigProperties.createChildProperties(propertyType, childDesc, val, parentConfigProperties))
			.collect(Collectors.toList());

		log.info("children :{}", children);

		propertiesService.createBulkChildProperties(children);

		//then
		List<ConfigProperties> findChild = propertiesService.findByParentId(parentConfigProperties.getId());
		log.info("findChild :{}", findChild);

		List<String> allDesc = children.stream()
			.map(ConfigProperties::getDescription)
			.collect(Collectors.toList());

		List<String> allSetValue = children.stream()
			.map(ConfigProperties::getSettingValue)
			.collect(Collectors.toList());

		List<Long> allSetValueAsLong = children.stream()
			.map(ConfigProperties::getSettingValueByLong)
			.collect(Collectors.toList());

		for (ConfigProperties configProperties : findChild) {
			assertAll(
				() -> assertThat(allDesc).contains(configProperties.getDescription()),
				() -> assertThat(allSetValue).contains(configProperties.getSettingValue()),
				() -> assertThat(allSetValueAsLong).contains(configProperties.getSettingValueByLong())
			);
		}
	}
}