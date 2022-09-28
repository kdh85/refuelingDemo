package com.example.refuelingdemo.api.domain;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseWriter extends BaseTime{

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;
}
