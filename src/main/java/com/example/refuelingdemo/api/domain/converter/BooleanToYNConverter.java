package com.example.refuelingdemo.api.domain.converter;

import java.util.Optional;

import javax.persistence.AttributeConverter;

public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {
	@Override
	public String convertToDatabaseColumn(Boolean attribute) {
		return Optional.ofNullable(attribute).
			orElse(Boolean.valueOf("N"))?"Y":"N";
	}

	@Override
	public Boolean convertToEntityAttribute(String ynFlag) {
		return "Y".equalsIgnoreCase(ynFlag);
	}
}
