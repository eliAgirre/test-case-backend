package com.vodafone.springboot.crud.domain;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.vodafone.springboot.crud.domain.model.SensorEventModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class SequenceGeneratorServiceImplTest {

    @InjectMocks
    private SequenceGeneratorServiceImpl sequenceGeneratorService;

    @Mock
    private MongoOperations mongoOperations;

    @Test
    void test_can_construct() {
        Assertions.assertDoesNotThrow(() ->
                new SequenceGeneratorServiceImpl(mongoOperations));
    }

    @Test
    void test_init(){
        Assertions.assertNotNull(sequenceGeneratorService);
    }

    @Test
    void test_generateSequence(){
        // Given
        String seqName = "sensor_122";

        // When
        Mockito.lenient().when(mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                SensorEventModel.DatabaseSequence.class)).thenReturn(new SensorEventModel.DatabaseSequence("sensor_12",6));

        // Then
        var result = sequenceGeneratorService.generateSequence(seqName);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("sensor_123", result);
    }

}