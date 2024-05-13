package com.vodafone.springboot.crud.utilities.jsonToObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class BaseJsonToObjectsCreator {

    protected <T> T getObjectFromFile(String resource, Class<T> clazz) throws IOException {
        File file = FileUtils.toFile(BaseJsonToObjectsCreator.class.getResource(resource));
        return new ObjectMapper().registerModule(new JavaTimeModule()).readValue(file, clazz);
    }

}
