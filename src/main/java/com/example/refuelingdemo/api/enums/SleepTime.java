package com.example.refuelingdemo.api.enums;

import lombok.Getter;

@Getter
public enum SleepTime {
	TIME_3000(3000L), TIME_100(100L);

	private final Long miles;

	SleepTime(Long miles) {
		this.miles = miles;
	}
}