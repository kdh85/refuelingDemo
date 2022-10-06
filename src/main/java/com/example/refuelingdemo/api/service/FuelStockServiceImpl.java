package com.example.refuelingdemo.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.annotaion.ExeTimer;
import com.example.refuelingdemo.annotaion.RedisLockCheck;
import com.example.refuelingdemo.annotaion.RedissonLockCheck;
import com.example.refuelingdemo.api.domain.FuelStock;
import com.example.refuelingdemo.api.domain.FuelStockHistory;
import com.example.refuelingdemo.api.repository.FuelStockHistoryRepository;
import com.example.refuelingdemo.api.repository.FuelStockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelStockServiceImpl implements FuelStockService {

	private final FuelStockRepository fuelStockRepository;

	private final FuelStockHistoryRepository fuelStockHistoryRepository;

	@Transactional
	@Override
	public void decreaseStock(final Long id, final Long useQuantity) {
		//변경감지를 위해 영속성 binding
		FuelStock fuelStock = getCurrentFuelStockById(id);
		//JPA 변경감지를 통해 자동으로 update 쿼리가 발생.
		fuelStock.decreaseStock(useQuantity);
		log.info("### call decreaseStock useQuantity:{}, fuelStock:{}", useQuantity, fuelStock);
		//재고변화 내역 저장.
		fuelStockHistoryRepository.save(
			FuelStockHistory.decreaseStockHistory(fuelStock.getTotalStock(), fuelStock.getRemainStock(),
				useQuantity, fuelStock));
	}

	protected FuelStock getCurrentFuelStockById(Long id) {
		return fuelStockRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("no data found"));
	}

	@Transactional
	@Override
	public FuelStock createFullStock(final Long totalStock) {
		FuelStock save = fuelStockRepository.save(FuelStock.fullStock(totalStock));
		//재고 생성내역 저장.
		fuelStockHistoryRepository.save(
			FuelStockHistory.createNewStockHistory(save.getTotalStock(), save.getRemainStock(), save));
		return save;
	}

	@Override
	@Transactional(readOnly = true)
	public FuelStock findStockInfoById(final Long id) {
		return getCurrentFuelStockById(id);
	}

	@Override
	public void deleteAllStock() {
		fuelStockRepository.deleteAll();

	}

	@Transactional
	@Override
	public void increaseRemainStock(Long id, Long chargeQuantity) {
		FuelStock fuelStock = getCurrentFuelStockById(id);
		fuelStock.increaseRemainStock(chargeQuantity);
		log.info("### call increaseRemainStock chargeQuantity:{}, fuelStock:{}", chargeQuantity, fuelStock);
		//재고 증가 내역 저장.
		fuelStockHistoryRepository.save(FuelStockHistory.increaseStockHistory(fuelStock.getTotalStock(),
			fuelStock.getRemainStock(), chargeQuantity, fuelStock));
	}

	@ExeTimer
	@RedisLockCheck
	@Transactional
	@Override
	public void decreaseStockByAOP(Long id, Long useQuantity) {
		this.decreaseStock(id, useQuantity);
	}

	@Transactional
	@Override
	public void removeStock(Long id) {
		FuelStock fuelStock = getCurrentFuelStockById(id);
		fuelStockRepository.delete(fuelStock);
		//재고 삭제 내역 저장.
		fuelStockHistoryRepository.delete(FuelStockHistory.removeStockHistory(fuelStock));
	}

	@ExeTimer
	@RedissonLockCheck
	@Transactional
	@Override
	public void decreaseStockByAOPV2(Long id, Long useQuantity) {
		this.decreaseStock(id, useQuantity);
	}

}
