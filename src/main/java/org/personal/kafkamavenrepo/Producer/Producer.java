package org.personal.kafkamavenrepo.Producer;


import org.personal.kafkamavenrepo.Domain.MongoDB.Events.Event;
import org.personal.kafkamavenrepo.Domain.MongoDB.ValueObjects.EventType;
import org.personal.kafkamavenrepo.Domain.Postgres.BusinessObjects.User;
import org.personal.kafkamavenrepo.Utilities.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends an event to the Kafka topic.
     *
     * @param type The type of the event (CREATION, DELETION, EDITION, etc.).
     * @param user The user object associated with the event.
     */
    public void sendEvent(EventType type, User user) {
        try {
            // Create and populate the event object
            Event event = new Event();
            event.setType(type);
            event.setDescription(JsonUtil.serialize(user));
            event.setTimestamp(LocalDateTime.now());

            // Serialize the event to JSON
            String serializedEvent = JsonUtil.serialize(event);

            // Send the event to the Kafka topic
            kafkaTemplate.send("test-topic", serializedEvent);
            logger.info("Successfully sent event: {}", serializedEvent);
        } catch (Exception e) {
            // Log the error with context
            logger.error("Failed to create or send event of type '{}' for user '{}'", type, user, e);
            throw new RuntimeException("Error sending event to Kafka", e); // Optionally rethrow
        }
    }
}