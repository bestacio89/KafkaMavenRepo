package org.personal.kafkamavenrepo.Service;

import org.personal.kafkamavenrepo.Domain.MongoDB.Events.Event;
import org.personal.kafkamavenrepo.Persistence.Mongo.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves an event by its name.
     *
     * @param eventName The name of the event.
     * @return The event if found, or null otherwise.
     */
    public Event getEventByName(String eventName) {
        try {
            Event event = eventRepository.findByEventName(eventName);
            if (event != null) {
                logger.info("Event found by name '{}': {}", eventName, event);
            } else {
                logger.warn("No event found with name '{}'", eventName);
            }
            return event;
        } catch (Exception e) {
            logger.error("Error retrieving event by name '{}'", eventName, e);
            throw new RuntimeException("Error retrieving event by name", e); // Optionally rethrow
        }
    }

    /**
     * Retrieves all events up to a specific timestamp.
     *
     * @param timestamp The timestamp up to which events should be retrieved.
     * @return A list of events.
     */
    public List<Event> getEventsUpTo(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }

        try {
            List<Event> events = eventRepository.findByTimestampBefore(timestamp);
            if (events.isEmpty()) {
                logger.info("No events found before timestamp '{}'", timestamp);
            } else {
                logger.info("Found {} events before timestamp '{}'", events.size(), timestamp);
            }
            return events;
        } catch (Exception e) {
            logger.error("Error retrieving events before timestamp '{}'", timestamp, e);
            throw new RuntimeException("Error retrieving events by timestamp", e); // Optionally rethrow
        }
    }

    /**
     * Creates a new event.
     *
     * @param event The event to be created.
     * @return The created event.
     */
    public Event createEvent(Event event) {
        try {
            event.setCreatedDate(LocalDateTime.now());
            event.setUpdatedDate(LocalDateTime.now());
            event.setDeleted(false);
            Event savedEvent = eventRepository.save(event);
            logger.info("Event created: {}", savedEvent);
            return savedEvent;
        } catch (Exception e) {
            logger.error("Error creating event: {}", event, e);
            throw new RuntimeException("Error creating event", e); // Optionally rethrow
        }
    }

    /**
     * Updates an existing event.
     *
     * @param id The ID of the event to update.
     * @param updatedEvent The event data to update.
     * @return The updated event, or null if not found.
     */
    public Event updateEvent(String id, Event updatedEvent) {
        try {
            Event existingEvent = eventRepository.findById(id).orElse(null);
            if (existingEvent != null) {
                existingEvent.setType(updatedEvent.getType());
                existingEvent.setDescription(updatedEvent.getDescription());
                existingEvent.setTimestamp(updatedEvent.getTimestamp());
                existingEvent.setUpdatedDate(LocalDateTime.now());
                Event savedEvent = eventRepository.save(existingEvent);
                logger.info("Event updated: {}", savedEvent);
                return savedEvent;
            } else {
                logger.warn("No event found with id '{}'", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error updating event with id '{}'", id, e);
            throw new RuntimeException("Error updating event", e); // Optionally rethrow
        }
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param id The ID of the event.
     * @return The event if found, or null otherwise.
     */
    public Event getEventById(String id) {
        try {
            Event event = eventRepository.findById(id).orElse(null);
            if (event != null) {
                logger.info("Event found by id '{}': {}", id, event);
            } else {
                logger.warn("No event found with id '{}'", id);
            }
            return event;
        } catch (Exception e) {
            logger.error("Error retrieving event by id '{}'", id, e);
            throw new RuntimeException("Error retrieving event by id", e); // Optionally rethrow
        }
    }

    /**
     * Retrieves all events.
     *
     * @return A list of all events.
     */
    public List<Event> getAllEvents() {
        try {
            List<Event> events = eventRepository.findAll();
            logger.info("Retrieved {} events", events.size());
            return events;
        } catch (Exception e) {
            logger.error("Error retrieving all events", e);
            throw new RuntimeException("Error retrieving all events", e); // Optionally rethrow
        }
    }

    /**
     * Marks an event as deleted by its ID.
     *
     * @param id The ID of the event to delete.
     */
    public void deleteEventById(String id) {
        try {
            Event event = eventRepository.findById(id).orElse(null);
            if (event != null) {
                event.setDeleted(true);
                event.setUpdatedDate(LocalDateTime.now());
                eventRepository.save(event);
                logger.info("Event with id '{}' marked as deleted", id);
            } else {
                logger.warn("No event found with id '{}' to delete", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting event with id '{}'", id, e);
            throw new RuntimeException("Error deleting event", e); // Optionally rethrow
        }
    }
}