package com.example.refuelingdemo.api.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.example.refuelingdemo.api.domain.converter.BooleanToYNConverter;
import com.example.refuelingdemo.api.enums.FuelType;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(exclude = "fuelStock")
@EqualsAndHashCode(callSuper = false, exclude = "fuelStock")
@Getter
@NoArgsConstructor
@Entity
@Table(indexes = {
	@Index(name = "idx_id_cust_id_Item_code_Item_name_full_type", columnList = "SEQ, custId, itemCode, itemName,fuelType"),
	@Index(name = "idx_id_cust_id", columnList = "SEQ, custId", unique = true)
})
@Slf4j
public class FuelItem extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SEQ")
	private Long id;

	private String custId;

	private String itemCode;

	private String itemName;

	@Enumerated(EnumType.STRING)
	private FuelType fuelType;

	private int unitPrice;

	@Convert(converter = BooleanToYNConverter.class)
	private boolean vatYn;

	@Convert(converter = BooleanToYNConverter.class)
	private boolean useYn;

	@Convert(converter = BooleanToYNConverter.class)
	private boolean delYn;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "STOCK_ID")
	@Setter
	private FuelStock fuelStock;

	@Builder
	private FuelItem(String custId, String itemCode, String itemName, FuelType fuelType, int unitPrice, boolean vatYn,
		boolean useYn,
		boolean delYn, FuelStock fuelStock) {
		this.custId = custId;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.fuelType = fuelType;
		this.unitPrice = unitPrice;
		this.vatYn = vatYn;
		this.useYn = useYn;
		this.delYn = delYn;
		this.fuelStock = fuelStock;
	}

	public static FuelItem createNewFurlItemWithStock(
		final String custId,
		final String itemCode,
		final String itemName,
		final FuelType fuelType,
		final int unitPrice,
		final Boolean vatYn,
		final Long totalStock
	) {
		return FuelItem.builder()
			.custId(custId)
			.itemCode(itemCode)
			.itemName(itemName)
			.fuelType(fuelType)
			.unitPrice(unitPrice)
			.vatYn(vatYn)
			.useYn(true)
			.delYn(false)
			.build()
			.addStockInfo(FuelStock.fullStock(totalStock));
	}

	private FuelItem addStockInfo(FuelStock fuelStock) {
		this.fuelStock = fuelStock;
		fuelStock.addItem(this);
		return this;
	}
}
