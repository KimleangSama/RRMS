package com.kkimleang.rrms.payload.response.room;

import com.kkimleang.rrms.entity.RoomAssignment;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.exception.ResourceDeletionException;
import com.kkimleang.rrms.payload.response.user.TenantResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
public class RoomAssignmentResponse {
    private UUID id;
    private RoomResponse room;
    private LocalDate assignmentDate;
    private LocalDate expectedEndDate;
    private TenantResponse user;
    private double rentalPrice;
    private String remark;
    private boolean hasPrivilege = false;

    private RoomAssignmentResponse() {
    }

    public static RoomAssignmentResponse fromRoomAssignment(User user, RoomAssignment assignment) {
        if (assignment.getDeletedAt() != null && assignment.getDeletedBy() != null) {
            throw new ResourceDeletionException("Room Assignment", assignment.getDeletedAt().toString());
        }
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
