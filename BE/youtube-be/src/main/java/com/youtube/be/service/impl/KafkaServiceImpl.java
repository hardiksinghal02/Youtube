package com.youtube.be.service.impl;

import com.youtube.be.service.QueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaServiceImpl implements QueueService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishMessage(String queueName, Object message) {
        try {
            kafkaTemplate.send(queueName, message);
        } catch (Exception e) {
            log.error("Error occurred : ", e);
        }
    }

    public void publishMessage(String queueName, String key, Object value) {
        try {
            kafkaTemplate.send(queueName, key, value);
            log.info("Message sent to kafka topic : " + queueName + ", message : " + value);
        } catch (Exception e) {
            log.error("Error occurred : ", e);
        }

    }

}
