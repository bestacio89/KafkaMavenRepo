package org.personal.kafkamavenrepo.Service;


import org.personal.kafkamavenrepo.Domain.MongoDB.Events.Event;
import org.personal.kafkamavenrepo.Domain.MongoDB.ValueObjects.EventType;
import org.personal.kafkamavenrepo.Domain.Postgres.BusinessObjects.User;
import org.personal.kafkamavenrepo.Persistence.Postgres.UserRepository;
import org.personal.kafkamavenrepo.Utilities.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessRebuildService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRebuildService.class);

    private final EventService eventService;
    private final UserRepository userRepository;

    @Autowired
    public BusinessRebuildService(EventService eventService, UserRepository userRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    /**
     * Rebuild the business state from events.
     */
    public void rebuildFromEvents() {
        logger.info("Starting to rebuild business state from events.");

        try {
            userRepository.deleteAll(); // Clear current state
            logger.info("All users deleted from the repository. Starting event replay.");

            List<Event> events = eventService.getAllEvents();

            for (Event event : events) {
                handleEvent(event);
            }

            logger.info("Rebuilding business state from events completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while rebuilding from events", e);
            throw new RuntimeException("Failed to rebuild from events", e); // Optionally rethrow or handle differently
        }
    }

    /**
     * Handle an individual event based on its type.
     *
     * @param event The event to handle.
     */
    public void handleEvent(Event event) {
        logger.info("Handling event: {}", event);

        try {
            EventType eventType = event.getType();

            switch (eventType) {
                case CREATION:
                    handleCreationEvent(event);
                    break;

                case DELETION:
                    handleDeletionEvent(event);
                    break;

                case EDITION:
                    handleEditionEvent(event);
                    break;

                case INTEGRATION:
                    handleIntegrationEvent(event);
                    break;

                default:
                    logger.error("Unknown event type encountered: {}", eventType);
                    throw new IllegalArgumentException("Unknown event type: " + eventType);
            }
        } catch (Exception e) {
            logger.error("Error handling event: {}", event, e);
            throw new RuntimeException("Failed to handle event", e); // Optionally rethrow or handle differently
        }
    }

    /**
     * Handle a user creation event.
     *
     * @param event The event containing user creation details.
     */
    private void handleCreationEvent(Event event) {
        User createdUser = JsonUtil.deserialize(event.getDescription(), User.class);
        userRepository.save(createdUser);
        logger.info("User created successfully from event: {}", createdUser);
    }

    /**
     * Handle a user deletion event.
     *
     * @param event The event containing user deletion details.
     */
    private void handleDeletionEvent(Event event) {
        User deletedUser = JsonUtil.deserialize(event.getDescription(), User.class);
        userRepository.delete(deletedUser);
        logger.info("User deleted successfully from event: {}", deletedUser);
    }

    /**
     * Handle a user edition event.
     *
     * @param event The event containing user edition details.
     */
    private void handleEditionEvent(Event event) {
        User editedUser = JsonUtil.deserialize(event.getDescription(), User.class);
        userRepository.save(editedUser);
        logger.info("User updated successfully from event: {}", editedUser);
    }

    /**
     * Handle an integration event.
     *
     * @param event The event related to an integration.
     */
    private void handleIntegrationEvent(Event event) {
        // Handle integration event
        logger.info("Integration event handled: {}", event);
    }
}