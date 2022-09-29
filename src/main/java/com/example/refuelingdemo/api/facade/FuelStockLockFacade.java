package com.example.refuelingdemo.api.facade;

import org.springframework.stereotype.Component;

import com.example.refuelingdemo.api.enums.SleepTime;
import com.example.refuelingdemo.api.repository.RedisRepository;
import com.example.refuelingdemo.api.service.FuelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FuelStockLockFacade {

	private final RedisRepository redisRepository;

	private final FuelService fuelService;

	@SuppressWarnings("BusyWait")
	public void decreaseFuelStock(final Long id, final Long useQuantity) throws InterruptedException {
		log.info("### call FuelStockLockFacade id:{}, useQuantity:{}", id, useQuantity);
		while (!redisRepository.isLock(id)) {
			Thread.sleep(SleepTime.TIME_100.getMiles());
		}

		try {
			fuelService.decreaseStock(id, useQuantity);
		} finally {
			redisRepository.unlock(id);
		}
	}
}
