package com.vodafone.springboot.crud.ports;

import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;
import com.vodafone.springboot.crud.utilities.exception.ResourceNotFoundException;

import java.util.List;

public interface SensorEventService {

    SensorEventModel create(SensorEventRqDto sensorEventRqDto);

    List<SensorEventModel> getAllSensorEvents();

    SensorEventModel getById(String sensorId) throws ResourceNotFoundException;

    SensorEventModel update(String sensorId, SensorEventRqDto sensorEventRqDto) throws ResourceNotFoundException;

    boolean delete(String sensorId) throws ResourceNotFoundException;

}
