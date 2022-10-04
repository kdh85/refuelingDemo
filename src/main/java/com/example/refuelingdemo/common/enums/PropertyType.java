package com.example.refuelingdemo.common.enums;

import lombok.Getter;

@Getter
public enum PropertyType {
	LATENCY("지연시간 설정값");
	
	private final String description;

	PropertyType(String description) {
		this.description = description;
	}
}
