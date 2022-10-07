package com.example.refuelingdemo.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(exclude = {"fuelItem", "fuelStockHistory"})
@EqualsAndHashCode(callSuper = false, exclude = {"fuelItem", "fuelStockHistory"})
@Getter
@NoArgsConstructor
@Entity
@Slf4j
public class FuelStock extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STOCK_ID")
	private Long id;

	private Long totalStock;

	private Long remainStock;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "fuelStock")
	private FuelItem fuelItem;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fuelStock", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<FuelStockHistory> fuelStockHistory = new ArrayList<>();

	@Builder
	private FuelStock(Long totalStock, Long remainStock) {
		this.totalStock = totalStock;
		this.remainStock = remainStock;
	}

	public void decreaseStock(Long useQuantity) {
		if (useQuantity > totalStock || this.remainStock - useQuantity < 0) {
			log.error("### 초과사용은 불가능합니다. ####");
			throw new IllegalArgumentException("초과사용은 불가능합니다.");
		}
		this.remainStock -= useQuantity;
	}

	public void increaseRemainStock(Long chargeQuantity) {
		if (chargeQuantity > totalStock && chargeQuantity + this.remainStock > this.totalStock) {
			log.error("### 최대 수량을 초과하여 재고를 채울수 없습니다.");
			throw new IllegalArgumentException("최대 수량을 초과하여 재고를 채울수 없습니다.");
		}
		this.remainStock += chargeQuantity;
	}

	public static FuelStock fullStock(final Long stock) {
		return FuelStock.builder()
			.remainStock(stock)
			.totalStock(stock)
			.build();
	}

	public void addItem(FuelItem fuelItem) {
		this.fuelItem = fuelItem;
	}
}
