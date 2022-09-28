package com.example.refuelingdemo.api.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.refuelingdemo.api.domain.FuelItem;

public interface FuelItemRepository extends JpaRepository<FuelItem, Long>, FuelItemRepositoryCustom {

	@EntityGraph(attributePaths = {"fuelStock"},type = EntityGraph.EntityGraphType.LOAD)
	FuelItem findByCustId(final String custId);
}
