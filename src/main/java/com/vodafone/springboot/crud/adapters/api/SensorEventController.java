package com.vodafone.springboot.crud.adapters.api;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.adapters.api.mapper.SensorEventsMapperImpl;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;
import com.vodafone.springboot.crud.ports.SensorEventService;
import com.vodafone.springboot.crud.utilities.exception.ResourceNotFoundException;
import com.vodafone.springboot.crud.utilities.constants.Constants;
import com.vodafone.springboot.crud.kafka.producer.MessageProducer;

@RestController
@RequestMapping("/api/sensor-events")
public class SensorEventController {

    private final SensorEventService sensorEventService;
    private final SensorEventsMapperImpl sensorEventMapper;
    private final MessageProducer messageProducer;

    public SensorEventController(SensorEventService sensorEventService,
                                 SensorEventsMapperImpl sensorEventMapper,
                                 MessageProducer messageProducer){
        this.sensorEventService = sensorEventService;
        this.sensorEventMapper = sensorEventMapper;
        this.messageProducer = messageProducer;
    }

    @GetMapping("/list-all")
    public List<SensorEventDto> getAllSensorEvents() {
        List<SensorEventModel> listSensorModel = sensorEventService.getAllSensorEvents();
        return sensorEventMapper.mapListModelsToListDto(listSensorModel);
    }

    @GetMapping(Constants.ENDPOINT_MAPPING_ID)
    public ResponseEntity<SensorEventDto> getSensorEventById(
                                            @PathVariable(value = Constants.PATH_VARIABLE_ID) String sensorId)
                                            throws ResourceNotFoundException {

        SensorEventModel model = sensorEventService.getById(sensorId);
        SensorEventDto rsDto = getRsDto(model);

        return ResponseEntity.ok().body(rsDto);
    }

    @PostMapping
    public ResponseEntity<SensorEventDto> createSensorEvent(@Valid @RequestBody SensorEventRqDto sensorEventRqDto) {
        SensorEventModel model = sensorEventService.create(sensorEventRqDto);
        SensorEventDto rsDto = getRsDto(model);
        if(!Objects.isNull(rsDto)){
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getSensorId());
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getTimestamp().toString());
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getType().toString());
            messageProducer.sendMessage(Constants.KAFKA_TOPIC, rsDto.getValue().toString());
        }
        return ResponseEntity.ok(rsDto);
    }

    @PutMapping("/update"+Constants.ENDPOINT_MAPPING_ID)
    public ResponseEntity<SensorEventDto> updateSensorEvent(@PathVariable(value = "id") String sensorId,
                                                            @Valid @RequestBody SensorEventRqDto sensorDto) throws ResourceNotFoundException {

        final SensorEventModel updatedModel = sensorEventService.update(sensorId, sensorDto);
        SensorEventDto updatedRsDto = getRsDto(updatedModel);

        return ResponseEntity.ok(updatedRsDto);
    }

    @DeleteMapping(Constants.ENDPOINT_MAPPING_ID)
    public Map<String, Boolean> deleteSensorEvent(
                                @PathVariable(value = Constants.PATH_VARIABLE_ID) String sensorId)
                                throws ResourceNotFoundException {

        boolean isDeleted = sensorEventService.delete(sensorId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", isDeleted);
        return response;
    }

    private SensorEventDto getRsDto(SensorEventModel model){
        return sensorEventMapper.mapModelToRto(model);
    }
}
