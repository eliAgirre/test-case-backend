package com.vodafone.springboot.crud.adapters.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vodafone.springboot.crud.adapters.api.mapper.SensorEventsMapperImpl;
import com.vodafone.springboot.crud.ports.SensorEventService;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;
import com.vodafone.springboot.crud.utilities.jsonToObject.JsonToObjectsCreator;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.utilities.exception.ResourceNotFoundException;
import com.vodafone.springboot.crud.utilities.enums.SensorTypeEnum;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.kafka.producer.MessageProducer;
import com.vodafone.springboot.crud.utilities.constants.ConstantsTest;

@ExtendWith(MockitoExtension.class)
class SensorEventControllerTest {

    @Autowired
    private JsonToObjectsCreator json;

    @InjectMocks
    private SensorEventController sensorEventController;

    @Mock
    private SensorEventService sensorEventService;

    @Mock
    private SensorEventsMapperImpl sensorEventsMapper;

    @Mock
    private MessageProducer messageProducer;

    @BeforeEach
    public void setUp() {
        json = new JsonToObjectsCreator();
    }

    @Test
    void test_can_construct() {
        Assertions.assertDoesNotThrow(() ->
                new SensorEventController(sensorEventService,
                                        sensorEventsMapper, messageProducer));
    }

    @Test
    void test_init(){
        Assertions.assertNotNull(sensorEventController);
    }

    @Test
    void test_createSensorEvent() throws IOException {
        // Given
        SensorEventRqDto requestDto = json.testSensorEventRqDtoPost();
        SensorEventDto dto = json.testSensorEventDto();
        SensorEventModel model = json.testSensorEventModel();

        // When
        Mockito.when(sensorEventService.create(requestDto)).thenReturn(model);
        Mockito.when(sensorEventsMapper.mapModelToRto(model)).thenReturn(dto);

        // Then
        var result = sensorEventController.createSensorEvent(requestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode(), ConstantsTest.STATUS_SHOULD_MATCH);
        Mockito.verify(sensorEventService, Mockito.times(1)).create(requestDto);
        Mockito.verify(sensorEventsMapper, Mockito.times(1)).mapModelToRto(model);

    }

    @Test
    void test_getAllSensorEvents() throws IOException {
        // Given
        SensorEventModel model = json.testSensorEventModel();
        SensorEventModel model2 = json.testSensorEventModel2();
        List<SensorEventModel> modelList = new ArrayList<>();
        modelList.add(model);
        modelList.add(model2);

        SensorEventDto dto = json.testSensorEventDto();
        SensorEventDto dto2 = json.testSensorEventDto2();
        List<SensorEventDto> dtoList = new ArrayList<>();
        dtoList.add(dto);
        dtoList.add(dto2);

        // When
        Mockito.when(sensorEventService.getAllSensorEvents()).thenReturn(modelList);
        Mockito.when(sensorEventsMapper.mapListModelsToListDto(modelList)).thenReturn(dtoList);

        // Then
        var result = sensorEventController.getAllSensorEvents();
        OffsetDateTime timestamp = OffsetDateTime.of(2024, 5,12,
                                    23,0,0,0, ZoneOffset.UTC);

        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(0, result.size());
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(ConstantsTest.SENSOR_ID_124, result.get(1).getSensorId());
        Assertions.assertEquals(timestamp, result.get(1).getTimestamp());
        Assertions.assertEquals(SensorTypeEnum.pressure, result.get(1).getType());
        Assertions.assertEquals(1002.0, result.get(1).getValue());
        Mockito.verify(sensorEventService, Mockito.times(1)).getAllSensorEvents();
        Mockito.verify(sensorEventsMapper, Mockito.times(1)).mapListModelsToListDto(modelList);
    }

    @Test
    void test_getSensorEventById() throws IOException, ResourceNotFoundException {
        // Given
        SensorEventModel model = json.testSensorEventModel();
        SensorEventDto dto = json.testSensorEventDto();
        String sensorId = model.getSensorId();

        // When
        Mockito.when(sensorEventService.getById(sensorId)).thenReturn(model);
        Mockito.when(sensorEventsMapper.mapModelToRto(model)).thenReturn(dto);

        // Then
        var result = sensorEventController.getSensorEventById(sensorId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode(), ConstantsTest.STATUS_SHOULD_MATCH);
        Mockito.verify(sensorEventService, Mockito.times(1)).getById(sensorId);
        Mockito.verify(sensorEventsMapper, Mockito.times(1)).mapModelToRto(model);
    }

    @Test
    void test_updateSensorEvent() throws IOException, ResourceNotFoundException {
        // Given
        SensorEventRqDto requestDto = json.testSensorEventRqDtoPut();
        SensorEventDto dto = json.testSensorEventDto();
        SensorEventModel model = json.testSensorEventModel();
        String sensorId = model.getSensorId();

        // When
        Mockito.when(sensorEventService.update(sensorId, requestDto)).thenReturn(model);
        Mockito.when(sensorEventsMapper.mapModelToRto(model)).thenReturn(dto);

        // Then
        var result = sensorEventController.updateSensorEvent(sensorId, requestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode(), ConstantsTest.STATUS_SHOULD_MATCH);
        Mockito.verify(sensorEventService, Mockito.times(1)).update(sensorId, requestDto);
        Mockito.verify(sensorEventsMapper, Mockito.times(1)).mapModelToRto(model);
    }

    @Test
    void test_deleteSensorEvent() throws IOException, ResourceNotFoundException {
        // Given
        SensorEventModel model2 = json.testSensorEventModel2();
        String sensorId = model2.getSensorId();
        boolean isDeleted = true;
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", isDeleted);

        // When
        Mockito.when(sensorEventService.delete(sensorId)).thenReturn(isDeleted);

        // Then
        var result = sensorEventController.deleteSensorEvent(sensorId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(response, result);
        Mockito.verify(sensorEventService, Mockito.times(1)).delete(sensorId);
    }

}