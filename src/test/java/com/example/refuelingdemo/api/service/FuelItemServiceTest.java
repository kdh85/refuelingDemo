package com.example.refuelingdemo.api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.refuelingdemo.api.domain.FuelItem;
import com.example.refuelingdemo.api.domain.FuelStock;
import com.example.refuelingdemo.api.dto.FuelItemCondition;
import com.example.refuelingdemo.api.dto.ResponseFuelItemDto;
import com.example.refuelingdemo.api.enums.FuelType;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FuelItemServiceTest {

	private static final String custId = "A0001";
	private static final String itemCode = "2209270001";
	private static final String itemName = FuelType.GASOLINE.getName();
	private static final FuelType fuelType = FuelType.GASOLINE;
	private static final int unitPrice = 10000;
	private static final Boolean vatYn = false;
	private static final Long totalStock = 100L;

	@Autowired
	private FuelItemService fuelItemService;

	private FuelItem saveFuelItem;

	@BeforeAll
	void setUp() {
		saveFuelItem = fuelItemService.createFuelItemWithStock(custId, itemCode, itemName, fuelType, unitPrice,
			vatYn, totalStock);
		log.info("hash :{}, saveFuelItem :{}", saveFuelItem.hashCode(), saveFuelItem);
	}

	@DisplayName("활성화된 신규제품을 등록 테스트")
	@Test
	void createNewItemTest() {

		log.info("saveFuelItem :{}", saveFuelItem);

		assertAll(
			() -> assertEquals(custId, saveFuelItem.getCustId()),
			() -> assertEquals(itemCode, saveFuelItem.getItemCode()),
			() -> assertEquals(itemName, saveFuelItem.getItemName()),
			() -> assertEquals(fuelType, saveFuelItem.getFuelType()),
			() -> assertEquals(unitPrice, saveFuelItem.getUnitPrice()),
			() -> assertFalse(saveFuelItem.isVatYn()),
			() -> assertTrue(saveFuelItem.isUseYn()),
			() -> assertFalse(saveFuelItem.isDelYn())
		);
	}

	@DisplayName("등록된 제품 및 제고 조회 테스트.(EntityGraph를 이용한 fetch 방식)")
	@Test
	void searchByCustIdTest() {

		FuelItem fuelItem = fuelItemService.findByCustId(custId);
		log.info("hash :{}, fuelItem :{}", fuelItem.hashCode(), fuelItem);
		FuelStock fuelStock = fuelItem.getFuelStock();
		log.info("hash :{}, fuelStock :{}", fuelStock.hashCode(), fuelStock);

		assertAll(
			() -> assertEquals(custId, fuelItem.getCustId()),
			() -> assertEquals(itemCode, fuelItem.getItemCode()),
			() -> assertEquals(itemName, fuelItem.getItemName()),
			() -> assertEquals(fuelType, fuelItem.getFuelType()),
			() -> assertEquals(unitPrice, fuelItem.getUnitPrice()),
			() -> assertFalse(fuelItem.isVatYn()),
			() -> assertTrue(fuelItem.isUseYn()),
			() -> assertFalse(fuelItem.isDelYn()),
			() -> assertEquals(fuelStock.getRemainStock(), totalStock),
			() -> assertEquals(fuelStock.getTotalStock(), totalStock)
		);
	}

	@DisplayName("등록된 제품 및 제고 조회 테스트.(JpaRepository  방식)")
	@Test
	@Transactional
	void searchByIdTest() {

		FuelItem fuelItem = fuelItemService.findById(saveFuelItem.getId());
		log.info("fuelItem :{}", fuelItem);
		FuelStock fuelStock = fuelItem.getFuelStock();
		log.info("fuelStock :{}", fuelStock);

		assertAll(
			() -> assertEquals(custId, fuelItem.getCustId()),
			() -> assertEquals(itemCode, fuelItem.getItemCode()),
			() -> assertEquals(itemName, fuelItem.getItemName()),
			() -> assertEquals(fuelType, fuelItem.getFuelType()),
			() -> assertEquals(unitPrice, fuelItem.getUnitPrice()),
			() -> assertFalse(fuelItem.isVatYn()),
			() -> assertTrue(fuelItem.isUseYn()),
			() -> assertFalse(fuelItem.isDelYn()),
			() -> assertEquals(fuelStock.getRemainStock(), totalStock),
			() -> assertEquals(fuelStock.getTotalStock(), totalStock)
		);
	}

	@Test
	void findFuelItemByConditionTest() {
		FuelItemCondition fuelItemCondition = FuelItemCondition.builder()
			.custId(custId)
			.itemCode(itemCode)
			.itemName(itemName)
			.fuelType(fuelType)
			.useYn(true)
			.build();

		ResponseFuelItemDto fuelItemByCondition = fuelItemService.findFuelItemByCondition(fuelItemCondition);
		log.info("hash :{}, fuelItem :{}", fuelItemByCondition.hashCode(), fuelItemByCondition);

		assertAll(
			() -> assertEquals(custId, fuelItemByCondition.getCustId()),
			() -> assertEquals(itemCode, fuelItemByCondition.getItemCode()),
			() -> assertEquals(itemName, fuelItemByCondition.getItemName()),
			() -> assertEquals(fuelType, fuelItemByCondition.getFuelType()),
			() -> assertEquals(unitPrice, fuelItemByCondition.getUnitPrice()),
			() -> assertFalse(fuelItemByCondition.isVatYn()),
			() -> assertTrue(fuelItemByCondition.isUseYn()),
			() -> assertFalse(fuelItemByCondition.isDelYn()),
			() -> assertEquals(fuelItemByCondition.getRemainStock(), totalStock),
			() -> assertEquals(fuelItemByCondition.getTotalStock(), totalStock)
		);
	}
}