package com.example.refuelingdemo.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.api.domain.FuelStock;
import com.example.refuelingdemo.api.repository.FuelStockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelStockService implements FuelService {

	private final FuelStockRepository fuelStockRepository;

	@Transactional
	@Override
	public void decreaseStock(final Long id, final Long useQuantity) {
		log.info("### call decreaseStock id:{}, useQuantity:{}",id, useQuantity);
		//변경감지를 위해 영속성 binding
		FuelStock fuelStock = fuelStockRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("no data found"));
		//JPA 변경감지를 통해 자동으로 update 쿼리가 발생.
		fuelStock.decreaseStock(useQuantity);
	}

	@Transactional
	@Override
	public FuelStock createFullStock(final Long totalStock){
		return fuelStockRepository.saveAndFlush(FuelStock.fullStock(totalStock));
	}

	@Override
	public FuelStock findStockInfoById(final Long id) {
		return fuelStockRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("no data found"));
	}

	@Override
	public void deleteAllStock() {
		fuelStockRepository.deleteAll();
	}
}