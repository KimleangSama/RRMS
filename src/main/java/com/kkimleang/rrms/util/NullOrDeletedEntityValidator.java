package com.kkimleang.rrms.util;

import com.kkimleang.rrms.exception.ResourceDeletionException;
import com.kkimleang.rrms.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

public class NullOrDeletedEntityValidator {
    public static <T> void validate(T entity, String entityName) {
        if (entity == null) {
            throw new ResourceNotFoundException(entityName, "id");
        }

        if (entity instanceof Deletable deletableEntity) {
            if (deletableEntity.getDeletedBy() != null && deletableEntity.getDeletedAt() != null) {
                throw new ResourceDeletionException(entityName, deletableEntity.getDeletedAt().toString());
            }
        }
    }

    public interface Deletable {
        UUID getDeletedBy();

        LocalDateTime getDeletedAt();
    }
}
