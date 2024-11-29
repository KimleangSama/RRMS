package com.kkimleang.rrms.payload.request.room;

import java.time.*;
import java.util.*;
import lombok.*;

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
