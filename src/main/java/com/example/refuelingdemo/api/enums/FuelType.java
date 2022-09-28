package com.example.refuelingdemo.api.enums;

import lombok.Getter;

@Getter
public enum FuelType {
	DIESEL("경유"),
	KEROSENE("등유"),
	GASOLINE("휘발유"),
	LPG("액화석유가스"),
	UREA_SOLUTION("요소수"),
	ETC("기타")
	;

	private final String name;

	FuelType(String name) {
		this.name = name;
	}
}
