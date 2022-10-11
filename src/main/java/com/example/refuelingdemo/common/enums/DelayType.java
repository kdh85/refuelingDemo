package com.example.refuelingdemo.common.enums;

import com.example.refuelingdemo.api.enums.SleepTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DelayType implements Type{
	SPIN_LOCK_DELAY(SleepTime.TIME_250),
	LOCK_DELAY(SleepTime.TIME_3000),
	TRY_LOCK_DELAY(SleepTime.TIME_2000)
	;

	private final SleepTime sleepTime;

	@Override
	public DelayType[] getValues(){
		return DelayType.values();
	}

	@Override
	public String getValue() {
		return this.name();
	}
}
