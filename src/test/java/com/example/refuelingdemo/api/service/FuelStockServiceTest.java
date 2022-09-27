package com.example.refuelingdemo.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.refuelingdemo.api.domain.FuelStock;
import com.example.refuelingdemo.config.facade.FuelStockLockFacade;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class FuelStockServiceTest {
	private final int threadCount = 102;
	private final long totalStock = 100L;
	private ExecutorService executorService;
	private CountDownLatch countDownLatch;
	@Autowired
	private FuelStockLockFacade fuelStockLockFacade;
	@Autowired
	private FuelStockService fuelStockService;

	private FuelStock fuelStock;

	@BeforeEach
	void beforeEach() {

		fuelStock = fuelStockService.createFullStock(totalStock);

		executorService = Executors.newFixedThreadPool(threadCount);
		countDownLatch = new CountDownLatch(threadCount);
	}

	@AfterEach
	public void afterEach() {
		fuelStockService.deleteAllStock();
	}

	@DisplayName("멀티쓰레드 환경에서 redis를 사용한 재고 감소에 대한 동시성 테스트")
	@Test
	void decreaseStockTest() throws InterruptedException {
		//when
		IntStream.range(0, threadCount).forEach(
			e -> executorService.submit(
				() -> {
					try {
						fuelStockLockFacade.decreaseFuelStock(fuelStock.getId(), 1L);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					} finally {
						countDownLatch.countDown();
					}
				}
			)
		);
		countDownLatch.await();
		// then
		FuelStock resultStockInfo = fuelStockService.findStockInfoById(1L);
		log.info("resultStockInfo : {}", resultStockInfo);
		assertAll(
			//잔여재고는 0이 되어야 한다.
			() -> assertThat(resultStockInfo.getRemainStock()).isEqualTo(0L),
			//초기재고는 100이 되어야 한다.
			() -> assertThat(resultStockInfo.getTotalStock()).isEqualTo(100L)
		);
	}

	@DisplayName("잔여 재고보다 초과로 사용시 에러를 반환 테스트")
	@Test
	void overUseStockReturnErrorTest() {
		assertThatThrownBy(
			() -> fuelStockLockFacade.decreaseFuelStock(fuelStock.getId(), 911L)
		).isInstanceOf(IllegalArgumentException.class);
	}
}