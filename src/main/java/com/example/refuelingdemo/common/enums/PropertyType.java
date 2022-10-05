package com.example.refuelingdemo.common.enums;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PropertyType implements Type{
	LATENCY("지연시간 설정값", Arrays.asList(DelayType.LOCK_DELAY, DelayType.SPIN_LOCK_DELAY));

	private final String description;

	private final List<DelayType> delayTypeList;

	public static List<DelayType> findByPropertyType(PropertyType propertyType){
		return Arrays.stream(PropertyType.values())
			.filter(type -> type == propertyType)
			.map(PropertyType::getDelayTypeList)
			.findAny()
			.orElseThrow(()-> new IllegalArgumentException("일치하는 PropertyType이 없습니다."));
	}

	@Override
	public PropertyType[] getValues() {
		return PropertyType.values();
	}

	@Override
	public String getValue() {
		return this.name();
	}
}
