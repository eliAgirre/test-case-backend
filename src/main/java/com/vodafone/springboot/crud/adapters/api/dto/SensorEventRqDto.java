package com.vodafone.springboot.crud.adapters.api.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.vodafone.springboot.crud.utilities.enums.SensorTypeEnum;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorEventRqDto {

    @NotNull
    @JsonProperty("type")
    private SensorTypeEnum type;

    @NotNull
    @JsonProperty("value")
    private Double value;
}
