package com.kkimleang.rrms.payload.response.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.response.user.*;

import java.time.*;
import java.util.UUID;

import lombok.*;

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
}
