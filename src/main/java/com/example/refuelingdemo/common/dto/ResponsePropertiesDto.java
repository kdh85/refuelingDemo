package com.example.refuelingdemo.common.dto;

import com.example.refuelingdemo.common.enums.Type;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ResponsePropertiesDto {

	private Long parentId;

	private Type propertyType;

	private String description;

	private String settingValue;

	@QueryProjection
	public ResponsePropertiesDto(Long parentId, Type propertyType, String description, String settingValue) {
		this.parentId = parentId;
		this.propertyType = propertyType;
		this.description = description;
		this.settingValue = settingValue;
	}

	public Long getSettingValueByLong() {
		return Long.parseLong(this.settingValue);
	}

}
