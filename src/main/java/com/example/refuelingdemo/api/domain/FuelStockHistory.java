package com.example.refuelingdemo.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.refuelingdemo.api.enums.StockType;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(exclude = "fuelStock")
@EqualsAndHashCode(callSuper = false, exclude = "fuelStock")
@Getter
@NoArgsConstructor
@Entity
@Slf4j
public class FuelStockHistory extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HISTORY_ID")
	private Long id;

	private Long totalStock;

	private Long remainStock;

	private Long useQuantity;

	@Enumerated(EnumType.STRING)
	private StockType stockType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STOCK_ID")
	private FuelStock fuelStock;

	@Builder
	private FuelStockHistory(final Long totalStock, final Long remainStock, final Long useQuantity,
		final StockType stockType,
		FuelStock fuelStock) {
		this.totalStock = totalStock;
		this.remainStock = remainStock;
		this.useQuantity = useQuantity;
		this.stockType = stockType;
		this.fuelStock = fuelStock;
	}

	public static FuelStockHistory createNewStockHistory(final Long totalStock, final Long remainStock,
		FuelStock fuelStock) {
		return FuelStockHistory.builder()
			.totalStock(totalStock)
			.remainStock(remainStock)
			.useQuantity(0L)
			.fuelStock(fuelStock)
			.stockType(StockType.DECREASE)
			.build();
	}

	public static FuelStockHistory decreaseStockHistory(final Long totalStock, final Long remainStock,
		final Long useQuantity, FuelStock fuelStock) {
		return FuelStockHistory.builder()
			.totalStock(totalStock)
			.remainStock(remainStock)
			.useQuantity(useQuantity)
			.fuelStock(fuelStock)
			.stockType(StockType.DECREASE)
			.build();
	}

	public static FuelStockHistory increaseStockHistory(final Long totalStock, final Long remainStock,
		final Long useQuantity, FuelStock fuelStock) {
		return FuelStockHistory.builder()
			.totalStock(totalStock)
			.remainStock(remainStock)
			.useQuantity(useQuantity)
			.fuelStock(fuelStock)
			.stockType(StockType.INCREASE)
			.build();
	}

	public static FuelStockHistory removeStockHistory(FuelStock fuelStock) {
		return FuelStockHistory.builder()
			.fuelStock(fuelStock)
			.stockType(StockType.REMOVE)
			.build();
	}
}
