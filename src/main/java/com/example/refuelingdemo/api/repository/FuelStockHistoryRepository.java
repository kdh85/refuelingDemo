package com.example.refuelingdemo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.refuelingdemo.api.domain.FuelStockHistory;

public interface FuelStockHistoryRepository extends JpaRepository<FuelStockHistory, Long> {
}
