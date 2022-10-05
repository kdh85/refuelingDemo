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

import com.example.refuelingdemo.common.domain.Properties;
import com.example.refuelingdemo.common.enums.PropertyType;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class PropertiesServiceTest {

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
		Properties parentProperties = propertiesService.createParentProperties(propertyType, parentDesc,
			parentSetValue);
		//then
		assertAll(
			() -> assertEquals(parentProperties.getType(), propertyType),
			() -> assertEquals(parentProperties.getDescription(), parentDesc),
			() -> assertEquals(parentProperties.getSettingValue(), parentSetValue)
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
		Properties parentProperties = propertiesService.createParentProperties(propertyType, parentDesc,
			parentSetValue);
		propertiesService.createChildProperties(propertyType, childDesc, childSetValue, parentProperties);

		//then
		Properties findChild = propertiesService.findByDescription(childDesc);
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
		Properties parentProperties = propertiesService.createParentProperties(propertyType, parentDesc,
			parentSetValue);

		List<Properties> children = childrenSetValue.stream()
			.map(val -> Properties.createChildProperties(propertyType, childDesc, val, parentProperties))
			.collect(Collectors.toList());

		log.info("children :{}", children);

		propertiesService.createBulkChildProperties(children);

		//then
		List<Properties> findChild = propertiesService.findByParentId(parentProperties.getId());
		log.info("findChild :{}", findChild);

		List<String> allDesc = children.stream()
			.map(Properties::getDescription)
			.collect(Collectors.toList());

		List<String> allSetValue = children.stream()
			.map(Properties::getSettingValue)
			.collect(Collectors.toList());

		List<Long> allSetValueAsLong = children.stream()
			.map(Properties::getSettingValueByLong)
			.collect(Collectors.toList());

		for (Properties properties : findChild) {
			assertAll(
				() -> assertThat(allDesc).contains(properties.getDescription()),
				() -> assertThat(allSetValue).contains(properties.getSettingValue()),
				() -> assertThat(allSetValueAsLong).contains(properties.getSettingValueByLong())
			);
		}
	}
}