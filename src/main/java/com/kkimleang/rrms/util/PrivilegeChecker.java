package com.kkimleang.rrms.util;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class PrivilegeChecker {
    public static boolean withoutRight(User user, UUID resource) {
        log.info("Checking privilege for user: {} and {}", user.getId(), resource);
        try {
            return !user.getId().equals(resource);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return true;
        }
    }

    public static void validateUser(CustomUserDetails user, String operation) {
        if (user == null || user.getUser() == null) {
            throw new ResourceForbiddenException("Unauthorized to " + operation, "No user details provided");
        }
    }
}
