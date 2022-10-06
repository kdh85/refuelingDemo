package com.example.refuelingdemo.common.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.refuelingdemo.common.domain.ConfigProperties;
import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.enums.DelayType;
import com.example.refuelingdemo.common.enums.PropertyType;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Slf4j
class ConfigPropertiesRepositoryTest {

	@Autowired
	private PropertiesRepository propertiesRepository;

	private ConfigProperties parent;

	@BeforeEach
	void setUp() {
		PropertyType propertyType = PropertyType.LATENCY;
		String parentDesc = "latency";
		String parentSetValue = "latencyValue";
		parent = ConfigProperties.createParentProperties(propertyType, parentDesc, parentSetValue);
		propertiesRepository.save(parent);
	}

	@ParameterizedTest
	@CsvSource(value = {"sleep3000:3000"}, delimiter = ':')
	void createChildTest(String childSetValue, String childDesc) {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		ConfigProperties child = ConfigProperties.createChildProperties(propertyType, childDesc, childSetValue, parent);
		//when
		ConfigProperties result = propertiesRepository.save(child);
		//then
		log.info("result : {}", result);

		assertAll(
			() -> assertEquals(result.getDescription(), childDesc),
			() -> assertEquals(result.getSettingValue(), childSetValue),
			() -> assertEquals(result.getParent(), parent)
		);
	}

	@ParameterizedTest
	@CsvSource(value = {"sleep3000:3000"}, delimiter = ':')
	void findAllChildByParentTypeTest(String childSetValue, String childDesc) {
		//given
		PropertyType propertyType = PropertyType.LATENCY;
		ConfigProperties child = ConfigProperties.createChildProperties(DelayType.TRY_LOCK_DELAY, childDesc,
			childSetValue, parent);
		ConfigProperties result = propertiesRepository.save(child);

		//when
		List<ResponsePropertiesDto> allChildByParentId = propertiesRepository.findAllChildByParentType(propertyType);

		//then
		log.info("result :{}", result);
		log.info("allChildByParentId :{}", allChildByParentId);
		assertAll(
			() -> assertEquals(allChildByParentId.get(0).getParentId(), result.getId()),
			() -> assertEquals(allChildByParentId.get(0).getDescription(), result.getDescription()),
			() -> assertEquals(allChildByParentId.get(0).getSettingValue(), result.getSettingValue()),
			() -> assertEquals(allChildByParentId.get(0).getPropertyType(), result.getPropertyType())
		);
	}
}