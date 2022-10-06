package com.example.refuelingdemo.common.repository;

import java.util.List;

import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.enums.Type;

public interface PropertiesRepositoryCustom {

	List<ResponsePropertiesDto> findAllChildByParentType(Type type);
}
