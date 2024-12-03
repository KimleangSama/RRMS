package com.kkimleang.rrms.util;

import static com.kkimleang.rrms.constant.PrivilegeLogErrorMessage.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class DeletableEntityValidator {
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

    public static void validateUser(CustomUserDetails user) {
        validate(user, "User");
        Optional.of(user)
                .map(CustomUserDetails::getUser)
                .orElseThrow(() -> new ResourceForbiddenException(FORBIDDEN, user));
    }
}
