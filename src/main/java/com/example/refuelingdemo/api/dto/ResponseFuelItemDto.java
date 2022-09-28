package com.example.refuelingdemo.api.dto;

import com.example.refuelingdemo.api.enums.FuelType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ResponseFuelItemDto {

	private Long itemId;
	private String custId;
	private String itemCode;
	private String itemName;
	private FuelType fuelType;
	private int unitPrice;
	private boolean vatYn;
	private boolean useYn;
	private boolean delYn;
	private Long stockId;
	private Long totalStock;
	private Long remainStock;

	@QueryProjection
	public ResponseFuelItemDto(Long itemId, String custId, String itemCode, String itemName, FuelType fuelType,
		int unitPrice, boolean vatYn, boolean useYn, boolean delYn, Long stockId, Long totalStock, Long remainStock) {
		this.itemId = itemId;
		this.custId = custId;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.fuelType = fuelType;
		this.unitPrice = unitPrice;
		this.vatYn = vatYn;
		this.useYn = useYn;
		this.delYn = delYn;
		this.stockId = stockId;
		this.totalStock = totalStock;
		this.remainStock = remainStock;
	}
}
