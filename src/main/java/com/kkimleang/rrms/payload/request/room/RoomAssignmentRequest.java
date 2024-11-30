package com.kkimleang.rrms.payload.request.room;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RoomAssignmentRequest {
    private UUID roomId;
    private LocalDate assignmentDate;
    private LocalDate expectedEndDate;
    private String assignmentCode;
    private double rentalPrice;
    private String remark;
}
