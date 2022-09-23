package com.example.refuelingdemo.api.dto;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.refuelingdemo.api.repository.QuantityRedisRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class QuantityTest {
	@Autowired
	private QuantityRedisRepository quantityRedisRepository;

	private Quantity fullStockQuantity;

	@BeforeEach
	void setUp() {
		String id = UUID.randomUUID().toString();
		Long totalQuantity = 100L;
		//재고 100프로 충전상태.
		fullStockQuantity = Quantity.FullQuantity(id, totalQuantity);
		log.info("fullStockQuantity = {}", fullStockQuantity);
		quantityRedisRepository.save(fullStockQuantity);
	}

	@Test
	public void 재고_등록기능_검증() {

		Quantity currentQuantity = quantityRedisRepository.findById(fullStockQuantity.getId())
			.orElseThrow(() -> new IllegalArgumentException("id에 대한 데이터가 없습니다."));

		assertAll(
			() -> assertThat(currentQuantity.getTotalStock()).isEqualTo(100),
			() -> assertThat(currentQuantity.getRemainStock()).isEqualTo(100),
			() -> assertThat(currentQuantity.getUseQuantity()).isEqualTo(0)
		);

	}

	@ParameterizedTest
	@CsvSource(value = {"100:2:98", "100:99:1", "100:100:0"}, delimiter = ':')
	public void 재고_변동기능_검증(String total, String use, String current) {
		Quantity currentQuantity = quantityRedisRepository.findById(fullStockQuantity.getId())
			.orElseThrow(() -> new IllegalArgumentException("id에 대한 데이터가 없습니다."));

		//급유결재 발생 및 유류 재고 변동.
		currentQuantity.refreshQuantity(Long.valueOf(use), currentQuantity.getTotalStock());

		log.info("calculate currentQuantity info : {}", currentQuantity);

		assertAll(
			() -> assertThat(currentQuantity.getTotalStock()).isEqualTo(Long.valueOf(total)),
			() -> assertThat(currentQuantity.getUseQuantity()).isEqualTo(Long.valueOf(use)),
			() -> assertThat(currentQuantity.getRemainStock()).isEqualTo(Long.valueOf(current))
		);
	}

	@Test
	public void 잔여재고_초과급유_에러반환_검증() {
		Quantity currentQuantity = quantityRedisRepository.findById(fullStockQuantity.getId())
			.orElseThrow(() -> new IllegalArgumentException("id에 대한 데이터가 없습니다."));

		//급유결재 발생 및 유류 재고 변동.
		assertThatThrownBy(
			() -> currentQuantity.refreshQuantity(101L, currentQuantity.getTotalStock())
		)
			.isInstanceOf(RuntimeException.class)
			.hasMessage("현재 잔여재고가 0보다 적을 수 없습니다.");
	}

	@Test
	public void 전체재고_초과_재고충전_에러반환_검증() {
		Quantity currentQuantity = quantityRedisRepository.findById(fullStockQuantity.getId())
			.orElseThrow(() -> new IllegalArgumentException("id에 대한 데이터가 없습니다."));

		//급유결재 발생 및 유류 재고 변동.
		assertThatThrownBy(
			() -> currentQuantity.refreshQuantity(-100L, currentQuantity.getTotalStock())
		)
			.isInstanceOf(RuntimeException.class)
			.hasMessage("전체 재고보다 현재 잔여재고가 더 많을 수 없습니다.");
	}
}