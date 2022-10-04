package com.example.refuelingdemo.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.refuelingdemo.common.domain.Properties;

public interface PropertiesRepository extends JpaRepository<Properties, Long> {

	Properties findByDescription(final String description);
}
