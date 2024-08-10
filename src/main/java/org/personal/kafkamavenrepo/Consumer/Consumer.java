package org.personal.kafkamavenrepo.Consumer;


import org.personal.kafkamavenrepo.Domain.MongoDB.Events.Event;
import org.personal.kafkamavenrepo.Service.BusinessRebuildService;
import org.personal.kafkamavenrepo.Utilities.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.kafka.support.KafkaHeaders;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;



@EnableKafka
@Component
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final BusinessRebuildService eventReplayService;

    public Consumer(BusinessRebuildService eventReplayService) {
        this.eventReplayService = eventReplayService;
    }

    @KafkaListener(topics = "test-topic", groupId = "my-group")
    public void listen(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        try {
            Event event = JsonUtil.deserialize(message, Event.class);
            eventReplayService.handleEvent(event);
            logger.info("Received event from topic {}: {}", topic, event);
        } catch (Exception e) {
            logger.error("Error processing message from topic {}: {}", topic, message, e);
            // Optionally, handle the error, e.g., send the message to a dead-letter topic
        }
    }
}