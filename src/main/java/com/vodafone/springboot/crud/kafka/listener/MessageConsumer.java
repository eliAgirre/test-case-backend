package com.vodafone.springboot.crud.kafka.listener;

import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

import com.vodafone.springboot.crud.utilities.constants.Constants;

@Component
public class MessageConsumer {

    @KafkaListener(topics = Constants.KAFKA_TOPIC, groupId = Constants.KAFKA_GROUP_ID)
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }

}
