package com.vodafone.springboot.crud.utilities.jsonToObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;

public class JsonToObjectsCreator extends BaseJsonToObjectsCreator {

    public SensorEventRqDto testSensorEventRqDtoPost() throws IOException {
        return getObjectFromFile("/json/test_sensor_event_rq_dto_post.json", SensorEventRqDto.class);
    }

    public SensorEventDto testSensorEventDto() throws IOException {
        return getObjectFromFile("/json/test_sensor_event_dto.json", SensorEventDto.class);
    }

    public SensorEventDto testSensorEventDto2() throws IOException {
        return getObjectFromFile("/json/test_sensor_event_dto2.json", SensorEventDto.class);
    }

    public SensorEventModel testSensorEventModel() throws IOException {
        return getObjectFromFile("/json/test_sensor_event_model.json", SensorEventModel.class);
    }

    public SensorEventModel testSensorEventModel2() throws IOException {
        return getObjectFromFile("/json/test_sensor_event_model2.json", SensorEventModel.class);
    }

    public SensorEventRqDto testSensorEventRqDtoPut() throws IOException {
        return getObjectFromFile("/json/test_sensor_event_rq_dto_put.json", SensorEventRqDto.class);
    }

}
