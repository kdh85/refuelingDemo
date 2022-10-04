package com.example.refuelingdemo.common.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.common.domain.Properties;
import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.repository.PropertiesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertiesService {

	private final PropertiesRepository propertiesRepository;

	@Transactional
	public Properties createParentProperties(final PropertyType propertyType, final String description, final String settingValue){
		return propertiesRepository.save(
			Properties.createParentProperties(propertyType, description, settingValue)
		);
	}

	@Transactional
	public void createChildProperties(final PropertyType propertyType, final String description, final String settingValue, Properties parent){
		propertiesRepository.save(
			Properties.createChildProperties(propertyType, description, settingValue, parent)
		);
	}

	@Transactional
	public void createBulkChildProperties(List<Properties> children){
		propertiesRepository.saveAll(children);
	}

	@Transactional(readOnly = true)
	public Properties findByDescription(final String description){
		return propertiesRepository.findByDescription(description);
	}

	@Transactional(readOnly = true)
	public List<Properties> findByParentId(final Long parentId){
		return propertiesRepository.findAllByParentId(parentId);
	}
}
