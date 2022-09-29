package com.example.refuelingdemo.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.refuelingdemo.api.domain.FuelStock;
import com.example.refuelingdemo.api.facade.FuelStockLockFacade;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class FuelStockServiceImplTest {
	private final int threadCount = 100;
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

	@DisplayName("멀티쓰레드 환경에서 redis를 사용한 재고 감소에 대한 동시성 테스트(facade 패턴버전)")
	@Test
	void decreaseStockTest() throws InterruptedException {
		//given
		createMultiThreadTest();
		//when
		FuelStock resultStockInfo = fuelStockService.findStockInfoById(fuelStock.getId());
		log.info("resultStockInfo : {}", resultStockInfo);
		//then
		assertAll(
			//잔여재고는 0이 되어야 한다.
			() -> assertThat(resultStockInfo.getRemainStock()).isEqualTo(0L),
			//초기재고는 100이 되어야 한다.
			() -> assertThat(resultStockInfo.getTotalStock()).isEqualTo(totalStock)
		);
	}

	private void createMultiThreadTest() throws InterruptedException {
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
	}

	@DisplayName("잔여 재고보다 초과로 사용시 에러를 반환 테스트")
	@Test
	void overUseStockReturnErrorTest() {
		assertThatThrownBy(
			() -> fuelStockLockFacade.decreaseFuelStock(fuelStock.getId(), 911L)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("기본 재고량 이하까지 재고충전 테스트")
	@Test
	void chargeRemainStockTest() throws InterruptedException {
		//given
		createMultiThreadTest();
		//when
		fuelStockService.increaseRemainStock(fuelStock.getId(), 5L);
		//then
		FuelStock resultStockInfo = fuelStockService.findStockInfoById(fuelStock.getId());
		log.info("resultStockInfo : {}", resultStockInfo);
		assertAll(
			//잔여재고는 100을 사용한뒤 5를 충전되기 때문에 5가 되어야 한다.
			() -> assertThat(resultStockInfo.getRemainStock()).isEqualTo(5L),
			//초기재고는 100이 되어야 한다.
			() -> assertThat(resultStockInfo.getTotalStock()).isEqualTo(totalStock)
		);
	}

	@DisplayName("기본 최대 재고를 초과하여 재고충전시 에럴르 반환 테스트")
	@Test
	void overChargeRemainStockErrorTest() {
		assertThatThrownBy(
			() -> fuelStockService.increaseRemainStock(fuelStock.getId(), totalStock + 1)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("멀티쓰레드 환경에서 redis를 사용한 재고 감소에 대한 동시성 테스트.(aop 버전)")
	@RepeatedTest(value = 10, name = "{displayName} : {currentRepetition}/{totalRepetitions}")
	void aopRedisLockTest() throws InterruptedException {
		AtomicReference<Long> count = new AtomicReference<>(0L);
		IntStream.range(0, threadCount).forEach(
			e -> executorService.submit(
				() -> {
					try {
						Long useQuantity = 1L;
						count.updateAndGet(v -> v + useQuantity);
						fuelStockService.decreaseStockByAOP(fuelStock.getId(), useQuantity);
					} finally {
						countDownLatch.countDown();
					}
				}
			)
		);
		countDownLatch.await();
		//then
		FuelStock resultStockInfo = fuelStockService.findStockInfoById(fuelStock.getId());
		log.info("resultStockInfo : {}",resultStockInfo);
		log.info("count :{}",count);
		assertAll(
			//잔여재고는 0이 되어야 한다.
			() -> assertThat(resultStockInfo.getRemainStock()).isEqualTo(0L),
			//초기재고는 100이 되어야 한다.
			() -> assertThat(resultStockInfo.getTotalStock()).isEqualTo(totalStock)
		);
	}
}