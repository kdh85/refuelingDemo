package com.example.refuelingdemo.api.dto;

import com.example.refuelingdemo.api.enums.FuelType;

import lombok.Builder;
import lombok.Data;

@Data
public class FuelItemCondition {

	private String custId;

	private String itemCode;

	private String itemName;

	private FuelType fuelType;

	private int unitPrice;

	private boolean vatYn;

	private boolean delYn;
	private boolean useYn;

	@Builder
	public FuelItemCondition(String custId, String itemCode, String itemName, FuelType fuelType, int unitPrice,
		boolean vatYn, boolean delYn, boolean useYn) {
		this.custId = custId;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.fuelType = fuelType;
		this.unitPrice = unitPrice;
		this.vatYn = vatYn;
		this.delYn = delYn;
		this.useYn = useYn;
	}
}
