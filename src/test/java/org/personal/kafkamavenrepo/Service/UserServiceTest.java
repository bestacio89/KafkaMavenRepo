package org.personal.kafkamavenrepo.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.personal.kafkamavenrepo.Config.KafkaTestConfig;
import org.personal.kafkamavenrepo.Domain.MongoDB.Events.Event;
import org.personal.kafkamavenrepo.Domain.MongoDB.ValueObjects.EventType;
import org.personal.kafkamavenrepo.Domain.Postgres.BusinessObjects.User;
import org.personal.kafkamavenrepo.TestKafkaMavenRepoApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {KafkaTestConfig.class, TestKafkaMavenRepoApplication.class})
@EmbeddedKafka(topics = {"event-topic"}, partitions = 1)
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CountDownLatch countDownLatch;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testUserCreationEvent() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        // Act
        userService.createUser(user);

        // Wait for the message to be consumed
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Capture the event from Kafka
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq("event-topic"), messageCaptor.capture());

        // Deserialize the captured message
        Event event = objectMapper.readValue(messageCaptor.getValue(), Event.class);

        // Assert
        assertEquals(EventType.CREATION, event.getType());
        assertTrue(event.getDescription().contains("testUser"));
        assertTrue(event.getDescription().contains("test@example.com"));
    }

    @Test
    public void testUserDeletionEvent() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user = userService.createUser(user);

        // Act
        userService.deleteUser(user.getId());

        // Wait for the message to be consumed
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Capture the event from Kafka
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq("event-topic"), messageCaptor.capture());

        // Deserialize the captured message
        Event event = objectMapper.readValue(messageCaptor.getValue(), Event.class);

        // Assert
        assertEquals(EventType.DELETION, event.getType());
        assertTrue(event.getDescription().contains("testUser"));
        assertTrue(event.getDescription().contains("test@example.com"));
    }
}
