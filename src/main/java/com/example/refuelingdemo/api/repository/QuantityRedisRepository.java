package com.example.refuelingdemo.api.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.refuelingdemo.api.dto.Quantity;

public interface QuantityRedisRepository extends CrudRepository<Quantity, String> {
}
