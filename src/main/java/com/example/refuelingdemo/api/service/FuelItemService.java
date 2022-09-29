package com.example.refuelingdemo.api.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.api.domain.FuelItem;

import com.example.refuelingdemo.api.dto.FuelItemCondition;
import com.example.refuelingdemo.api.dto.ResponseFuelItemDto;
import com.example.refuelingdemo.api.enums.FuelType;
import com.example.refuelingdemo.api.repository.FuelItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelItemService {

	private final FuelItemRepository fuelItemRepository;

	@Transactional
	public FuelItem createFuelItemWithStock(
		final String custId,
		final String itemCode,
		final String itemName,
		final FuelType fuelType,
		final int unitPrice,
		final Boolean vatYn,
		final Long totalStock
	) {
		return fuelItemRepository.save(
			FuelItem.createNewFurlItemWithStock(custId, itemCode, itemName, fuelType, unitPrice, vatYn, totalStock));
	}

	@Transactional(readOnly = true)
	public FuelItem findByCustId(final String custId) {
		log.info("### findByCustId id:{}", custId);
		return Optional.of(fuelItemRepository.findByCustId(custId))
			.orElseThrow(() -> new IllegalArgumentException("custId에 대한 데이터가 없습니다."));
	}

	@Transactional(readOnly = true)
	public FuelItem findById(final Long id){
		return fuelItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id에 대한 데이터가 없습니다."));
	}

	@Transactional(readOnly = true)
	public ResponseFuelItemDto findFuelItemByCondition(FuelItemCondition fuelItemCondition){
		return fuelItemRepository.findFuelItemByCondition(fuelItemCondition);
	}
}
