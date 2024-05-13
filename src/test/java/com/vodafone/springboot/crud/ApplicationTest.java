package com.vodafone.springboot.crud;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.Assertions;

import com.vodafone.springboot.crud.ports.SensorEventService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private SensorEventService sensorEventService;

    @Test
    public void contextLoads() {
        // Test if the application context loads successfully
    }

    @Test
    public void test_sensorEventService(){
        var result = sensorEventService.getAllSensorEvents();
        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(0, result.size());
    }
}