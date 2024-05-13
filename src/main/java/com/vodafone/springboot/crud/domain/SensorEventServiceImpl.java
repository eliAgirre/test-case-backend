package com.vodafone.springboot.crud.domain;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.adapters.api.mapper.SensorEventsMapperImpl;
import com.vodafone.springboot.crud.ports.SequenceGeneratorService;
import com.vodafone.springboot.crud.ports.SensorEventService;
import com.vodafone.springboot.crud.adapters.database.repository.SensorEventRepository;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;
import com.vodafone.springboot.crud.utilities.exception.ResourceNotFoundException;
import com.vodafone.springboot.crud.utilities.constants.Constants;

@Service
@Slf4j
public class SensorEventServiceImpl implements SensorEventService {
    private final SequenceGeneratorService sequenceGeneratorService;
    private final SensorEventsMapperImpl sensorEventMapper;
    private final SensorEventRepository sensorEventRepository;

    public SensorEventServiceImpl(SequenceGeneratorService sequenceGeneratorService,
                                  SensorEventsMapperImpl sensorEventMapper,
                                  SensorEventRepository sensorEventRepository){
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.sensorEventMapper = sensorEventMapper;
        this.sensorEventRepository = sensorEventRepository;
    }

    public SensorEventModel create(SensorEventRqDto sensorEventRqDto){
        SensorEventDto dto = sensorEventMapper.mapRqDtoToDto(sensorEventRqDto);
        SensorEventModel model = sensorEventMapper.mapDtoToModel(dto);
        model.setSensorId(sequenceGeneratorService.generateSequence(SensorEventModel.SEQUENCE_NAME));
        model.setTimestamp(LocalDateTime.now());
        return sensorEventRepository.save(model);
    }

    public List<SensorEventModel> getAllSensorEvents(){
        return sensorEventRepository.findAll();
    }

    public SensorEventModel getById(String sensorId)
                                throws ResourceNotFoundException {

        return sensorEventRepository.findById(sensorId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ID_NOT_FOUND + sensorId));
    }

    public SensorEventModel update(String sensorId, SensorEventRqDto sensorEventRqDto)
                                                throws ResourceNotFoundException {

        SensorEventModel updatedSensor = new SensorEventModel();

        if(!Objects.isNull(this.getById(sensorId))){
            SensorEventModel modelFound = this.getById(sensorId);
            SensorEventDto dto = sensorEventMapper.mapRqDtoToDto(sensorEventRqDto);
            SensorEventModel model = sensorEventMapper.mapDtoToModel(dto);
            model.setSensorId(modelFound.getSensorId());
            model.setTimestamp(LocalDateTime.now());
            model.setType(sensorEventRqDto.getType());
            model.setValue(sensorEventRqDto.getValue());
            updatedSensor = sensorEventRepository.save(model);
        }

        return updatedSensor;
    }

    public boolean delete(String sensorId)
            throws ResourceNotFoundException {

        if(!Objects.isNull(this.getById(sensorId))){
            sensorEventRepository.delete(this.getById(sensorId));
        }

        return true;
    }

}
