package org.personal.kafkamavenrepo.Service;


import org.personal.kafkamavenrepo.Domain.MongoDB.ValueObjects.EventType;
import org.personal.kafkamavenrepo.Domain.Postgres.BusinessObjects.User;
import org.personal.kafkamavenrepo.Persistence.Postgres.UserRepository;
import org.personal.kafkamavenrepo.Producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final Producer producer;

    @Autowired
    public UserService(UserRepository userRepository, Producer producer) {
        this.userRepository = userRepository;
        this.producer = producer;
    }

    public User createUser(User user) {
        logger.info("Attempting to create user: {}", user);

        try {
            user.setCreatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            // Publish an event
            producer.sendEvent(EventType.CREATION, savedUser);

            logger.info("User created successfully: {}", savedUser);
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user: {}", user, e);
            // Optionally rethrow or handle differently
            throw e;
        }
    }

    public void deleteUser(Long userId) {
        logger.info("Attempting to delete user with id: {}", userId);

        try {
            User userToDelete = userRepository.findById(userId).orElse(null);
            if (userToDelete != null) {
                userRepository.deleteById(userId);
                // Publish an event
                producer.sendEvent(EventType.DELETION, userToDelete);
                logger.info("User deleted successfully: {}", userToDelete);
            } else {
                logger.warn("No user found with id '{}'", userId);
            }
        } catch (Exception e) {
            logger.error("Error deleting user with id '{}'", userId, e);
            // Optionally rethrow or handle differently
            throw e;
        }
    }

    public User getUserByUsername(String username) {
        logger.info("Attempting to retrieve user by username: {}", username);

        try {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                logger.info("User found by username '{}': {}", username, user);
            } else {
                logger.warn("No user found with username '{}'", username);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error retrieving user by username '{}'", username, e);
            // Optionally rethrow or handle differently
            throw e;
        }
    }
}