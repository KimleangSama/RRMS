package com.kkimleang.rrms.payload.request.room;

import java.time.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateRoomAssignmentRequest {
    private UUID roomId;
    private LocalDateTime assignmentDate;
    private LocalDateTime expectedEndDate;
    private String assignmentCode;
    private double rentalPrice;
    private String remark;
}
