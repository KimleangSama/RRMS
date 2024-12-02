package com.kkimleang.rrms.util;

import com.kkimleang.rrms.exception.*;
import java.time.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class NullOrDeletedEntityValidator {
    public static <T> void validate(T entity, String entityName) {
        if (entity == null) {
            throw new ResourceNotFoundException(entityName, "id");
        }
        if (entity instanceof Deletable deletableEntity) {
            if (deletableEntity.getDeletedBy() != null || deletableEntity.getDeletedAt() != null) {
                throw new ResourceDeletionException(entityName, deletableEntity.getDeletedAt().toString());
            }
        }
    }

    public interface Deletable {
        UUID getDeletedBy();

        Instant getDeletedAt();
    }
}
