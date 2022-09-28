package com.example.refuelingdemo.api.repository;

import com.example.refuelingdemo.api.dto.FuelItemCondition;
import com.example.refuelingdemo.api.dto.ResponseFuelItemDto;

public interface FuelItemRepositoryCustom {
	ResponseFuelItemDto findFuelItemByCondition(FuelItemCondition fuelItemCondition);
}
