package com.kkimleang.rrms.util;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class PrivilegeChecker {
    public static boolean isPropertyOwner(User user, Property property) {
        try {
            return user.getId().equals(property.getUser().getId());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return true;
        }
    }

    public static boolean isRoomOwner(User user, Room room) {
        try {
            return user.getId().equals(room.getProperty().getUser().getId());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return false;
        }
    }

    public static boolean isRoomAssignmentOwner(User user, RoomAssignment roomAssignment) {
        try {
            return user.getId().equals(roomAssignment.getRoom().getProperty().getUser().getId());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return false;
        }
    }

    public static boolean isRoomAssignmentTenant(User user, RoomAssignment roomAssignment) {
        try {
            return user.getId().equals(roomAssignment.getUser().getId());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return false;
        }
    }

    public static boolean isCreator(User user, UUID creatorId) {
        try {
            return user.getId().equals(creatorId);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return false;
        }
    }

    public static void validateUser(CustomUserDetails user, String operation) {
        if (user == null || user.getUser() == null) {
            throw new ResourceForbiddenException("Unauthorized to " + operation, "No user details provided");
        }
    }
}
