package com.example.refuelingdemo.common.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.common.domain.ConfigProperties;
import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.enums.Type;
import com.example.refuelingdemo.common.repository.PropertiesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertiesService {

	private final PropertiesRepository propertiesRepository;

	@Transactional
	public ConfigProperties createParentProperties(final PropertyType propertyType, final String description, final String settingValue){
		return propertiesRepository.save(
			ConfigProperties.createParentProperties(propertyType, description, settingValue)
		);
	}

	@Transactional
	public void createChildProperties(final PropertyType propertyType, final String description, final String settingValue, ConfigProperties parent){
		propertiesRepository.save(
			ConfigProperties.createChildProperties(propertyType, description, settingValue, parent)
		);
	}

	@Transactional
	public void createBulkChildProperties(List<ConfigProperties> children){
		propertiesRepository.saveAll(children);
	}

	@Transactional(readOnly = true)
	public ConfigProperties findByDescription(final String description){
		return propertiesRepository.findByDescription(description);
	}

	@Transactional(readOnly = true)
	public List<ConfigProperties> findByParentId(final Long parentId){
		return propertiesRepository.findAllByParentId(parentId);
	}

	@Transactional(readOnly = true)
	public List<ResponsePropertiesDto> findAllChildByParentType(final Type type){
		return propertiesRepository.findAllChildByParentType(type);
	}
}
