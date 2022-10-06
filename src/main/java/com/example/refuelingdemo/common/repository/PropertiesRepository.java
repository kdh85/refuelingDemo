package com.example.refuelingdemo.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.refuelingdemo.common.domain.ConfigProperties;

public interface PropertiesRepository extends JpaRepository<ConfigProperties, Long>, PropertiesRepositoryCustom {

	ConfigProperties findByDescription(final String description);

	@Query(value = "select p from ConfigProperties p where p.parent.id= :parentId")
	List<ConfigProperties> findAllByParentId(@Param("parentId") final Long parentId);
}
