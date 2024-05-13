package com.vodafone.springboot.crud.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.vodafone.springboot.crud.adapters.api.mapper.SensorEventsMapperImpl;
import com.vodafone.springboot.crud.adapters.database.repository.SensorEventRepository;
import com.vodafone.springboot.crud.ports.SequenceGeneratorService;
import com.vodafone.springboot.crud.utilities.jsonToObject.JsonToObjectsCreator;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;
import com.vodafone.springboot.crud.utilities.enums.SensorTypeEnum;
import com.vodafone.springboot.crud.utilities.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class SensorEventServiceImplTest {

    @Autowired
    private JsonToObjectsCreator json;

    @InjectMocks
    private SensorEventServiceImpl sensorEventService;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private SensorEventsMapperImpl sensorEventMapper;

    @Mock
    private SensorEventRepository sensorEventRepository;

    @BeforeEach
    public void setUp() {
        json = new JsonToObjectsCreator();
    }

    @Test
    void test_can_construct() {
        Assertions.assertDoesNotThrow(() ->
                new SensorEventServiceImpl(sequenceGeneratorService,
                                            sensorEventMapper, sensorEventRepository));
    }

    @Test
    void test_init(){
        Assertions.assertNotNull(sensorEventService);
    }

    @Test
    void test_create() throws IOException {
        getService();

        // Given
        SensorEventRqDto requestDto = json.testSensorEventRqDtoPost();
        SensorEventDto dto = json.testSensorEventDto();
        SensorEventModel model = json.testSensorEventModel();

        // When
        Mockito.when(sensorEventMapper.mapRqDtoToDto(requestDto)).thenReturn(dto);
        Mockito.when(sensorEventMapper.mapDtoToModel(dto)).thenReturn(model);
        Mockito.when(sequenceGeneratorService.generateSequence("users_sequence")).thenReturn("sensor_123");
        Mockito.when(sensorEventRepository.save(model)).thenReturn(model);

        // Then
        var result = sensorEventService.create(requestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("sensor_123", result.getSensorId());
        Assertions.assertEquals(SensorTypeEnum.temperature, result.getType());
        Assertions.assertEquals(25.3, result.getValue());
    }

    @Test
    void test_getAllSensorEvents() throws IOException {
        getService();

        // Given
        SensorEventModel model = json.testSensorEventModel();
        SensorEventModel model2 = json.testSensorEventModel2();
        List<SensorEventModel> modelList = new ArrayList<>();
        modelList.add(model);
        modelList.add(model2);

        // When
        Mockito.when(sensorEventRepository.findAll()).thenReturn(modelList);

        // Then
        var result = sensorEventService.getAllSensorEvents();
        String str = "2024-05-12T23:00:00.000";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime timestamp = LocalDateTime.parse(str, formatter);

        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(0, result.size());
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("sensor_124", result.get(1).getSensorId());
        Assertions.assertEquals(timestamp, result.get(1).getTimestamp());
        Assertions.assertEquals(SensorTypeEnum.pressure, result.get(1).getType());
        Assertions.assertEquals(1002.0, result.get(1).getValue());
    }

    @Test
    void test_getById_given_sensorId_then_returns_sensor_event_model() throws IOException, ResourceNotFoundException {
        getService();

        // Given
        String sensorId = "sensor_123";
        SensorEventModel model = json.testSensorEventModel();

        // When
        Mockito.when(sensorEventRepository.findById(sensorId)).thenReturn(Optional.ofNullable(model));

        // Then
        Assertions.assertNotNull(sensorEventService.getById(sensorId));
    }

    @Test
    void test_updateById() throws IOException, ResourceNotFoundException {
        getService();

        SensorEventModel sensorCreated = this.test_create2();

        // Given
        String sensorId = sensorCreated.getSensorId();
        SensorEventRqDto requestDto = json.testSensorEventRqDtoPut();
        SensorEventDto dto = json.testSensorEventDto();
        SensorEventModel model = json.testSensorEventModel();

        // When
        Mockito.when(sensorEventMapper.mapRqDtoToDto(requestDto)).thenReturn(dto);
        Mockito.when(sensorEventMapper.mapDtoToModel(dto)).thenReturn(model);
        Mockito.when(sensorEventRepository.findById(sensorId)).thenReturn(Optional.ofNullable(model));
        Mockito.when(sensorEventRepository.save(Objects.requireNonNull(model))).thenReturn(model);

        // Then
        var result = sensorEventService.update(sensorId, requestDto);
        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(SensorTypeEnum.temperature, result.getType());
        Assertions.assertEquals(SensorTypeEnum.proximity, result.getType());
        Assertions.assertEquals(12, result.getValue());
    }

    @Test
    void test_delete() throws IOException, ResourceNotFoundException {
        getService();

        // Given
        String sensorId = "sensor_124";
        SensorEventModel model2 = json.testSensorEventModel2();

        // Then
        Mockito.when(sensorEventRepository.findById(sensorId)).thenReturn(Optional.ofNullable(model2));

        // Then
        var result = sensorEventService.delete(sensorId);
        Assertions.assertEquals(Boolean.TRUE, result);
    }

    private SensorEventModel test_create2() throws IOException {
        getService();

        // Given
        SensorEventRqDto requestDto = json.testSensorEventRqDtoPost();
        SensorEventDto dto = json.testSensorEventDto();
        SensorEventModel model = json.testSensorEventModel();

        // When
        Mockito.when(sensorEventMapper.mapRqDtoToDto(requestDto)).thenReturn(dto);
        Mockito.when(sensorEventMapper.mapDtoToModel(dto)).thenReturn(model);
        Mockito.when(sequenceGeneratorService.generateSequence("users_sequence")).thenReturn("sensor_123");
        Mockito.when(sensorEventRepository.save(model)).thenReturn(model);

        // Then
        var result = sensorEventService.create(requestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("sensor_123", result.getSensorId());
        Assertions.assertEquals(SensorTypeEnum.temperature, result.getType());
        Assertions.assertEquals(25.3, result.getValue());
        return result;
    }

    private void getService(){
        this.sensorEventService = new SensorEventServiceImpl(sequenceGeneratorService,
                sensorEventMapper, sensorEventRepository);
    }

}