package com.kkimleang.rrms.util;

import com.kkimleang.rrms.entity.Role;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.exception.ResourceForbiddenException;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class PrivilegeChecker {
    public static boolean withoutRight(User user, UUID resource) {
        log.info("Checking privilege for user: {} and {}", user.getId(), resource);
        try {
            boolean isOwner = user.getId().equals(resource);
            boolean isAdmin;
            for (Role role : user.getRoles()) {
                isAdmin = role.getName().equals("ADMIN") || role.getName().equals("SUPER_ADMIN");
                if (isOwner && isAdmin) {
                    return false;
                }
            }
            return true;
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
