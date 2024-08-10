package org.personal.kafkamavenrepo.Utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    // Generic method to serialize an object to JSON string
    public static <T> String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object: {}", object, e);
            throw new RuntimeException("Error serializing object", e);
        }
    }

    // Generic method to deserialize JSON string to an object
    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing JSON: {} to class: {}", json, clazz.getName(), e);
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }
}
