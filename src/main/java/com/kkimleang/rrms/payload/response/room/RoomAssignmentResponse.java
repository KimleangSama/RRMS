package com.kkimleang.rrms.payload.response.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.response.user.*;
import java.time.*;
import lombok.*;

@Getter
@Setter
@ToString
public class RoomAssignmentResponse {
    private RoomResponse room;
    private LocalDate assignmentDate;
    private LocalDate expectedEndDate;
    private TenantResponse user;
    private double rentalPrice;
    private String remark;

    private RoomAssignmentResponse() {
    }

    public static RoomAssignmentResponse fromRoomAssignment(RoomAssignment assignment) {
        RoomAssignmentResponse response = new RoomAssignmentResponse();
        response.setRoom(RoomResponse.fromRoom(assignment.getRoom()));
        response.setAssignmentDate(assignment.getAssignmentDate());
        response.setExpectedEndDate(assignment.getExpectedEndDate());
        response.setUser(TenantResponse.fromUser(assignment.getUser()));
        response.setRentalPrice(assignment.getRentalPrice());
        response.setRemark(assignment.getRemark());
        return response;
    }
}
