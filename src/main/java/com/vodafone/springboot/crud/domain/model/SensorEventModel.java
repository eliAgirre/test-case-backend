package com.vodafone.springboot.crud.domain.model;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import com.vodafone.springboot.crud.utilities.enums.SensorTypeEnum;

@Document(collection = "sensor_events")
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorEventModel {

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Id
    @NotBlank
    @JsonProperty("sensorId")
    private String sensorId;

    @NotNull
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @NotNull
    @JsonProperty("type")
    private SensorTypeEnum type;

    @NotNull
    @JsonProperty("value")
    private Double value;

    @Document(collection = "database_sequences")
    @Data
    @AllArgsConstructor
    public static class DatabaseSequence {

        @Id
        @JsonProperty("id")
        private String id;

        @JsonProperty("seq")
        private long seq;

    }
}
