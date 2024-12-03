package com.kkimleang.rrms.payload.response.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.response.user.*;
import com.kkimleang.rrms.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
public class RoomAssignmentResponse {
    private UUID id;
    private RoomResponse room;
    private LocalDateTime assignmentDate;
    private LocalDateTime expectedEndDate;
    private TenantResponse user;
    private double rentalPrice;
    private String remark;
    private boolean hasPrivilege = false;

    private RoomAssignmentResponse() {
    }

    public static RoomAssignmentResponse fromRoomAssignment(User user, RoomAssignment assignment) {
        DeletableEntityValidator.validate(assignment, "Room Assignment");
        RoomAssignmentResponse response = new RoomAssignmentResponse();
        response.setId(assignment.getId());
        response.setRoom(RoomResponse.fromRoom(assignment.getRoom()));
        response.setAssignmentDate(assignment.getAssignmentDate());
        response.setExpectedEndDate(assignment.getExpectedEndDate());
        response.setUser(TenantResponse.fromUser(assignment.getUser()));
        response.setRentalPrice(assignment.getRentalPrice());
        response.setRemark(assignment.getRemark());
        if (assignment.getUser().getId().equals(user.getId())) {
            response.setHasPrivilege(true);
        }
        return response;
    }

    public static List<RoomAssignmentResponse> fromRoomAssignments(User user, List<RoomAssignment> roomAssignments) {
        List<RoomAssignmentResponse> responses = new ArrayList<>();
        for (RoomAssignment assignment : roomAssignments) {
            try {
                responses.add(fromRoomAssignment(user, assignment));
            } catch (ResourceDeletionException e) {
                log.debug(e.getMessage());
            }
        }
        return responses;
    }
}
