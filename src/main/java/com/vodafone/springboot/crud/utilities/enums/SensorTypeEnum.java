package com.vodafone.springboot.crud.utilities.enums;

import static com.vodafone.springboot.crud.utilities.constants.Constants.TEMPERATURE;
import static com.vodafone.springboot.crud.utilities.constants.Constants.PRESSURE;
import static com.vodafone.springboot.crud.utilities.constants.Constants.PROXIMITY;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SensorTypeEnum {
    temperature(TEMPERATURE),
    pressure(PRESSURE),
    proximity(PROXIMITY);

    private final String value;
    SensorTypeEnum(String value) {
        this.value = value;
    }
    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

}
