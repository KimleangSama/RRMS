package com.kkimleang.rrms.util;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class PrivilegeChecker {
    public static boolean hasRight(User user, UUID resource) {
        log.info("Checking privilege for user: {} and {}", user.getId(), resource);
        try {
            boolean isOwner = user.getId().equals(resource);
            boolean isAdmin;
            for (Role role : user.getRoles()) {
                isAdmin = role.getName().equals("ADMIN") || role.getName().equals("SUPER_ADMIN");
                if (isOwner && isAdmin) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return false;
        }
    }
}
