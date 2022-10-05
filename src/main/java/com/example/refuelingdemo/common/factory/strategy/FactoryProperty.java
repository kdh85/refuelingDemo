package com.example.refuelingdemo.common.factory.strategy;

import com.example.refuelingdemo.common.enums.PropertyType;

public interface FactoryProperty {
	void createPropertiesInfo(PropertyType latency);

	void createPropertiesBulkInfo(PropertyType latency);
}
