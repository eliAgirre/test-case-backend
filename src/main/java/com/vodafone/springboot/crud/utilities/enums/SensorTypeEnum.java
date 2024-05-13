package com.vodafone.springboot.crud.utilities.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SensorTypeEnum {

    temperature("temperature"),
    pressure("pressure"),
    proximity("proximity"),
    humidity("humidity");

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
