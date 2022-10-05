package com.example.refuelingdemo.common.factory;

import org.springframework.stereotype.Component;

import com.example.refuelingdemo.common.factory.strategy.LatencyFactoryProperty;
import com.example.refuelingdemo.common.factory.strategy.FactoryProperty;
import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.service.PropertiesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PropertiesFactory {

	private final PropertiesService propertiesService;
	public FactoryProperty getPropertyType(PropertyType propertyType){
		log.info("### PropertiesFactory call type :{}",propertyType);

		if(propertyType.equals(PropertyType.LATENCY)){
			return new LatencyFactoryProperty(propertiesService);
		}
		throw new IllegalArgumentException("유효한 PropertyType이 없습니다.");
	}
}
