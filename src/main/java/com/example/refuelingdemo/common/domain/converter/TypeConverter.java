package com.example.refuelingdemo.common.domain.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		List<Type> allPropertiesType = createAllPropertiesType();

		return allPropertiesType.stream()
			.filter(type -> type.getValue().equalsIgnoreCase(dbData))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("일치하는 Type 이 없습니다."));
	}

	private static List<Type> createAllPropertiesType() {
		List<Type> joinedType = new ArrayList<>();
		joinedType.addAll(Arrays.asList(PropertyType.values()));
		joinedType.addAll(Arrays.asList(DelayType.values()));
		return joinedType;
	}
}
