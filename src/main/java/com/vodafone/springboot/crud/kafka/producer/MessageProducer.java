package com.vodafone.springboot.crud.kafka.producer;

import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class MessageProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String sensorEventDto) {
        kafkaTemplate.send(topic, sensorEventDto);
    }

}
