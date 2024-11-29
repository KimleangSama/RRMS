package com.kkimleang.rrms.payload.request.room;

import com.kkimleang.rrms.enums.room.AvailableStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class EditAvailableRequest {
    private AvailableStatus availableStatus;
    private LocalDate availableDate;
}
