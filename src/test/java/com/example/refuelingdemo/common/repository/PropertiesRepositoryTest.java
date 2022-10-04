package com.example.refuelingdemo.common.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.refuelingdemo.common.domain.Properties;
import com.example.refuelingdemo.common.enums.PropertyType;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Slf4j
class PropertiesRepositoryTest {

	@Autowired
	private PropertiesRepository propertiesRepository;

	private Properties parent;

	@BeforeEach
	void setUp() {
		PropertyType propertyType = PropertyType.LATENCY;
		String parentDesc = "latency";
		String parentSetValue = "latencyValue";
		parent = Properties.createParentProperties(propertyType, parentDesc, parentSetValue);
		propertiesRepository.save(parent);
	}

	@ParameterizedTest
	@CsvSource(value = {"sleep3000:3000"}, delimiter = ':')
	void createChildTest(String childSetValue, String childDesc) {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		Properties child = Properties.createChildProperties(propertyType, childDesc, childSetValue, parent);
		//when
		Properties result = propertiesRepository.save(child);
		//then
		log.info("result : {}", result);

		assertAll(
			() -> assertEquals(result.getDescription(), childDesc),
			() -> assertEquals(result.getSettingValue(), childSetValue),
			() -> assertEquals(result.getParent(), parent)
		);
	}
}