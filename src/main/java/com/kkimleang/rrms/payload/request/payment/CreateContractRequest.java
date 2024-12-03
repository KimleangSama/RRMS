package com.kkimleang.rrms.payload.request.payment;

import com.kkimleang.rrms.enums.room.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateContractRequest {
    private UUID roomAssignmentId;
    private String name;
    private LocalDateTime signedDate;
    private LocalDateTime expiryDate;
    private String contractFileUrl;
    private ContractStatus contractStatus;
}
