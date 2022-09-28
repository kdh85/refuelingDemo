package com.example.refuelingdemo.api.repository;

import static com.example.refuelingdemo.api.domain.QFuelItem.*;
import static com.example.refuelingdemo.api.domain.QFuelStock.*;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.example.refuelingdemo.api.dto.FuelItemCondition;
import com.example.refuelingdemo.api.dto.QResponseFuelItemDto;
import com.example.refuelingdemo.api.dto.ResponseFuelItemDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FuelItemRepositoryCustomImpl implements FuelItemRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	public FuelItemRepositoryCustomImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	public ResponseFuelItemDto findFuelItemByCondition(FuelItemCondition fuelItemCondition) {
		return jpaQueryFactory.select(
				new QResponseFuelItemDto(
					fuelItem.id,
					fuelItem.custId,
					fuelItem.itemCode,
					fuelItem.itemName,
					fuelItem.fuelType,
					fuelItem.unitPrice,
					fuelItem.vatYn,
					fuelItem.useYn,
					fuelItem.delYn,
					fuelStock.id,
					fuelStock.totalStock,
					fuelStock.remainStock
				)
			)
			.from(fuelItem)
			.join(fuelItem.fuelStock, fuelStock)
			.where(
				custIdEq(fuelItemCondition),
				itemCodeEq(fuelItemCondition),
				itemNameEq(fuelItemCondition),
				fuelTypeEq(fuelItemCondition),
				vatYnEq(fuelItemCondition),
				delYnEq(fuelItemCondition),
				useYnEq(fuelItemCondition)
			)
			.fetchOne();

	}

	private static BooleanExpression custIdEq(FuelItemCondition fuelItemCondition) {
		return Optional.ofNullable(fuelItemCondition.getCustId()).isPresent() ?
			fuelItem.custId.eq(fuelItemCondition.getCustId()) : null;
	}

	private static BooleanExpression itemCodeEq(FuelItemCondition fuelItemCondition) {
		return Optional.ofNullable(fuelItemCondition.getItemCode()).isPresent() ?
			fuelItem.itemCode.eq(fuelItemCondition.getItemCode()) : null;
	}

	private static BooleanExpression itemNameEq(FuelItemCondition fuelItemCondition) {
		return Optional.ofNullable(fuelItemCondition.getItemName()).isPresent() ?
			fuelItem.itemName.eq(fuelItemCondition.getItemName()) : null;
	}

	private static BooleanExpression fuelTypeEq(FuelItemCondition fuelItemCondition) {
		return Optional.ofNullable(fuelItemCondition.getFuelType()).isPresent() ?
			fuelItem.fuelType.eq(fuelItemCondition.getFuelType()) : null;
	}

	private static BooleanExpression vatYnEq(FuelItemCondition fuelItemCondition) {
		return fuelItem.vatYn.eq(fuelItemCondition.isVatYn());
	}

	private static BooleanExpression delYnEq(FuelItemCondition fuelItemCondition) {
		return fuelItem.delYn.eq(fuelItemCondition.isDelYn());
	}

	private static BooleanExpression useYnEq(FuelItemCondition fuelItemCondition) {
		return fuelItem.useYn.eq(fuelItemCondition.isUseYn());
	}
}
