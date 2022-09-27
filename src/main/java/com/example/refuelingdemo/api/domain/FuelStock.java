package com.example.refuelingdemo.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Getter
@NoArgsConstructor
@Entity
@Slf4j
public class FuelStock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long totalStock;

	private Long remainStock;

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

	public static FuelStock fullStock(final Long stock) {
		return FuelStock.builder()
			.remainStock(stock)
			.totalStock(stock)
			.build();
	}
}
