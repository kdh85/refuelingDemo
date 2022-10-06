package com.example.refuelingdemo.api.facade;

import static com.example.refuelingdemo.common.enums.PropertyType.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.refuelingdemo.annotaion.ExeTimer;
import com.example.refuelingdemo.api.enums.SleepTime;
import com.example.refuelingdemo.api.repository.RedisRepository;
import com.example.refuelingdemo.api.service.FuelStockService;
import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.enums.DelayType;
import com.example.refuelingdemo.common.service.PropertiesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FuelStockLockFacade {

	private final RedisRepository redisRepository;

	private final FuelStockService fuelStockService;

	private final PropertiesService propertiesService;

	@SuppressWarnings("BusyWait")
	@ExeTimer
	public void decreaseFuelStock(final Long id, final Long useQuantity) throws InterruptedException {
		log.info("### call FuelStockLockFacade id:{}, useQuantity:{}", id, useQuantity);
		try {
			List<ResponsePropertiesDto> allChildByParentType = propertiesService.findAllChildByParentType(LATENCY);

			Long lockDelay = allChildByParentType.stream()
				.filter(p -> p.getPropertyType() == DelayType.LOCK_DELAY)
				.map(ResponsePropertiesDto::getSettingValueByLong)
				.findFirst()
				.orElse(SleepTime.TIME_3000.getMiles());

			Long spinDelay = allChildByParentType.stream()
				.filter(p -> p.getPropertyType() == DelayType.SPIN_LOCK_DELAY)
				.map(ResponsePropertiesDto::getSettingValueByLong)
				.findFirst()
				.orElse(SleepTime.TIME_200.getMiles());

			while (!redisRepository.isLock(id, lockDelay)) {
				Thread.sleep(spinDelay);
			}
			fuelStockService.decreaseStock(id, useQuantity);
		} finally {
			redisRepository.unlock(id);
		}
	}
}
