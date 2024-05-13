package com.vodafone.springboot.crud.adapters.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.json.JSONException;
import org.json.JSONObject;

import com.vodafone.springboot.crud.Application;
import com.vodafone.springboot.crud.utilities.constants.ConstantsTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SensorEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_createSensorEvent() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ConstantsTest.BASE_MAPPING_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ConstantsTest.REQUEST_BODY_TEST_INT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println("created: "+mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void test_getSensorEventById() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ConstantsTest.BASE_MAPPING_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ConstantsTest.REQUEST_BODY_TEST_INT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Extract JSON response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        String sensorId = getSensorIdFromRespBody(responseBody);

        MvcResult mvcResultGet = mockMvc.perform(MockMvcRequestBuilders.get(ConstantsTest.BASE_MAPPING_ENDPOINT+"/{id}", sensorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println("get details with id: "+sensorId
                +" and details: "+mvcResultGet.getResponse().getContentAsString());
    }

    @Test
    public void test_updateSensorEventById() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ConstantsTest.BASE_MAPPING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ConstantsTest.REQUEST_BODY_TEST_INT))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();

        // Extract JSON response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("created: "+responseBody);
        String sensorId = getSensorIdFromRespBody(responseBody);

        String requestBodyPut = "{ \"type\": \"proximity\", \"value\": 12 }";

        MvcResult mvcResultUpdated = mockMvc.perform(MockMvcRequestBuilders.put(ConstantsTest.BASE_MAPPING_ENDPOINT+"/update/{id}", sensorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyPut))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();

        System.out.println("updated: "+mvcResultUpdated.getResponse().getContentAsString());
    }

    @Test
    public void test_deleteSensorEventById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(ConstantsTest.BASE_MAPPING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ConstantsTest.REQUEST_BODY_TEST_INT))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();

        // Extract JSON response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        String sensorId = getSensorIdFromRespBody(responseBody);
        System.out.println("get id: "+sensorId
                +" and details: "+responseBody);

        MvcResult mvcResultDeleted = mockMvc.perform(MockMvcRequestBuilders.delete(ConstantsTest.BASE_MAPPING_ENDPOINT+"/{id}", sensorId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();

        System.out.println("result: "+mvcResultDeleted.getResponse().getContentAsString());
    }

    private String getSensorIdFromRespBody(String responseBody) throws JSONException {
        JSONObject sourceObject = new JSONObject(responseBody);
        String sensorId;
        Object result = sourceObject.get("sensorId");
        sensorId = result.toString();
        return sensorId;
    }

}
