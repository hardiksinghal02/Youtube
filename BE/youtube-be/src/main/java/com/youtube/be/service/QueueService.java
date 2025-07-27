package com.youtube.be.service;

public interface QueueService {

    void publishMessage(String queueName, Object value);

    void publishMessage(String queueName, String key, Object value);

}
