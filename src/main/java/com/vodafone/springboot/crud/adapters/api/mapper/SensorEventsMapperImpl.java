package com.vodafone.springboot.crud.adapters.api.mapper;

import org.springframework.stereotype.Component;

import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;

@Component
public class SensorEventsMapperImpl extends SensorEventMapper {

    @Override
    public SensorEventDto mapRqDtoToDto(SensorEventRqDto request) {
        if ( request == null ) {
            return null;
        }

        SensorEventDto sensorEventDto = new SensorEventDto();
        sensorEventDto.setType( request.getType() );
        sensorEventDto.setValue(request.getValue() );

        return sensorEventDto;
    }

    @Override
    public SensorEventModel mapDtoToModel(SensorEventDto request) {
        if ( request == null ) {
            return null;
        }

        SensorEventModel sensorEventModel = new SensorEventModel();
        sensorEventModel.setSensorId( request.getSensorId() );
        sensorEventModel.setTimestamp( mapToLocalDateTime(request.getTimestamp()) );
        sensorEventModel.setType( request.getType() );
        sensorEventModel.setValue(request.getValue() );

        return sensorEventModel;
    }

    @Override
    public SensorEventDto mapModelToRto(SensorEventModel model) {
        if ( model == null ) {
            return null;
        }

        SensorEventDto sensorEventDto = new SensorEventDto();
        sensorEventDto.setSensorId( model.getSensorId() );
        sensorEventDto.setTimestamp( mapToOffsetDateTime(model.getTimestamp()) );
        sensorEventDto.setType( model.getType() );
        sensorEventDto.setValue(model.getValue() );

        return sensorEventDto;
    }
}
