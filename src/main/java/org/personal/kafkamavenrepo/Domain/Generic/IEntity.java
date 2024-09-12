package org.personal.kafkamavenrepo.domain.Generic;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface IEntity<ID extends Serializable> {
    ID getId();
    void setId(ID id);

    LocalDateTime getCreatedDate();
    void setCreatedDate(LocalDateTime createdDate);

    LocalDateTime getUpdatedDate();
    void setUpdatedDate(LocalDateTime updatedDate);

    boolean isDeleted();
    void setDeleted(boolean deleted);
}