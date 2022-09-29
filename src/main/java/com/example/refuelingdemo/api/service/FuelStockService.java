package com.example.refuelingdemo.api.service;

import com.example.refuelingdemo.api.domain.FuelStock;

public interface FuelStockService {

	void decreaseStock(final Long id, final Long useQuantity);

	FuelStock createFullStock(final Long totalStock);

	FuelStock findStockInfoById(final Long id);

	void deleteAllStock();

	void increaseRemainStock(final Long id, final Long chargeQuantity);

	void decreaseStockByAOP(Long id, Long useQuantity);
}
