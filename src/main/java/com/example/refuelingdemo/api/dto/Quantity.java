package com.example.refuelingdemo.api.dto;

import java.time.LocalDateTime;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
@RedisHash("quantity")
public class Quantity {

	@Id
	private final String id;

	private Long useQuantity;

	private Long totalStock;

	private Long remainStock;

	@Setter
	private LocalDateTime refreshTime;

	@Builder
	public Quantity(String id, Long useQuantity, Long totalStock) {
		this.id = id;
		this.useQuantity = useQuantity;
		this.totalStock = totalStock;
		this.remainStock = totalStock;
		this.refreshTime = LocalDateTime.now();
	}

	public void refreshQuantity(Long useQuantity, Long totalQuantity) {
		if (LocalDateTime.now().isAfter(this.refreshTime)) {
			this.useQuantity = useQuantity;
			this.totalStock = totalQuantity;
			this.remainStock = getRemainStock(useQuantity);
		}
	}

	private long getRemainStock(Long useQuantity) {
		long newRemainStock = remainStock - useQuantity;
		log.info("===> totalStock : {}, remainStock : {}, useQuantity : {}, newRemainStock : {}", totalStock,
			remainStock, useQuantity, newRemainStock);
		validationRemainStock(newRemainStock);
		return newRemainStock;
	}

	private void validationRemainStock(long newRemainStock) {
		if (totalStock < newRemainStock) {
			throw new RuntimeException("전체 재고보다 현재 잔여재고가 더 많을 수 없습니다.");
		}
		if (newRemainStock < 0) {
			throw new RuntimeException("현재 잔여재고가 0보다 적을 수 없습니다.");
		}
	}

	public static Quantity FullQuantity(final String id, final Long quantity) {
		return Quantity.builder()
			.id(id)
			.useQuantity(0L)
			.totalStock(quantity)
			.build();
	}
}
