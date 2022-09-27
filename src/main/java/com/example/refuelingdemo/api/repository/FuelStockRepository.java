package com.example.refuelingdemo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.refuelingdemo.api.domain.FuelStock;

public interface FuelStockRepository extends JpaRepository<FuelStock, Long> {
}
