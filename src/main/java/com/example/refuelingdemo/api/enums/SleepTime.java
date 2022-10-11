package com.example.refuelingdemo.api.enums;

import lombok.Getter;

@Getter
public enum SleepTime {
	TIME_3000(3000L),
	TIME_2000(2000L),
	TIME_1000(1000L),
	TIME_250(250L),
	TIME_200(200L),
	TIME_100(100L),
	TIME_10(10L)
	;

	private final Long miles;

	SleepTime(Long miles) {
		this.miles = miles;
	}

	public String getMilesToString(){
		return this.miles.toString();
	}
}
