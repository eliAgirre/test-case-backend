package com.vodafone.springboot.crud.adapters.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;

import com.vodafone.springboot.crud.utilities.enums.SensorTypeEnum;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorEventDto {

    @Id
    @JsonProperty("sensorId")
    private String sensorId;

    @JsonProperty("timestamp")
    private OffsetDateTime timestamp;

    @JsonProperty("type")
    private SensorTypeEnum type;

    @JsonProperty("value")
    private Double value;
}
