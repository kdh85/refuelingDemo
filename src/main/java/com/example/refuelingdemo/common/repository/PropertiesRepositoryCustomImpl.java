package com.example.refuelingdemo.common.repository;


import java.util.List;

import javax.persistence.EntityManager;

import com.example.refuelingdemo.common.domain.QConfigProperties;
import com.example.refuelingdemo.common.dto.QResponsePropertiesDto;
import com.example.refuelingdemo.common.dto.ResponsePropertiesDto;
import com.example.refuelingdemo.common.enums.Type;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertiesRepositoryCustomImpl implements PropertiesRepositoryCustom{

	private final JPAQueryFactory jpaQueryFactory;

	public PropertiesRepositoryCustomImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public List<ResponsePropertiesDto> findAllChildByParentType(Type type) {
		Long parentId = jpaQueryFactory.select(
				QConfigProperties.configProperties.id
			)
			.from(QConfigProperties.configProperties)
			.where(QConfigProperties.configProperties.propertyType.eq(type))
			.fetchOne();

		log.info("#### parentId :{}",parentId);

		return jpaQueryFactory.select(
				new QResponsePropertiesDto(
					QConfigProperties.configProperties.id,
					QConfigProperties.configProperties.propertyType,
					QConfigProperties.configProperties.description,
					QConfigProperties.configProperties.settingValue
				)
			)
			.from(QConfigProperties.configProperties)
			.where(
				QConfigProperties.configProperties.parent.id.eq(parentId)
			)
			.fetch();
	}
}
