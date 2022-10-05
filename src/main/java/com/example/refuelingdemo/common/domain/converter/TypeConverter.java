package com.example.refuelingdemo.common.domain.converter;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import com.example.refuelingdemo.common.enums.DelayType;
import com.example.refuelingdemo.common.enums.PropertyType;
import com.example.refuelingdemo.common.enums.Type;

public class TypeConverter implements AttributeConverter<Type, String> {

	@Override
	public String convertToDatabaseColumn(Type attribute) {
		return Arrays.stream(attribute.getValues())
			.filter(delayType -> delayType == attribute)
			.map(Type::getValue)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("일치하는 DelayType 이 없습니다."));
	}

	@Override
	public Type convertToEntityAttribute(String dbData) {
		return Stream.concat(
				Arrays.stream(PropertyType.values()),
				Arrays.stream(DelayType.values())
			)
			.filter(type -> type.getValue().equalsIgnoreCase(dbData))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("일치하는 Type 이 없습니다."));
	}
}
