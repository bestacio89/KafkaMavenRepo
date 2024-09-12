package org.personal.kafkamavenrepo.domain.MongoDB.Events;

import lombok.Getter;
import lombok.Setter;
import org.personal.kafkamavenrepo.domain.Generic.IEntity;
import org.personal.kafkamavenrepo.domain.MongoDB.ValueObjects.EventType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "events")
public class Event implements IEntity<String> {

    // Getters and Setters
    @Setter
    @Getter
    @Id
    private String id;
    @Setter
    @Getter
    private EventType type;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private LocalDateTime timestamp;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private boolean deleted;

    @Override
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
