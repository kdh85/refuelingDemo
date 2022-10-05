package com.example.refuelingdemo.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.refuelingdemo.common.domain.Properties;
import com.example.refuelingdemo.common.enums.Type;

public interface PropertiesRepository extends JpaRepository<Properties, Long> {

	Properties findByDescription(final String description);

	@Query("select p from Properties p where p.parent.id= :parentId")
	List<Properties> findAllByParentId(@Param("parentId") final Long parentId);

	List<Properties> findAllByTypeAndParentIsNull(Type type);
}
