package com.kkimleang.rrms.payload.request.payment;

import com.kkimleang.rrms.enums.room.*;
import java.time.*;
import lombok.*;

@Getter
@Setter
@ToString
public class EditInvoiceInfoRequest {
    private LocalDateTime dueDate;
    private String remark;
    private InvoiceStatus invoiceStatus;
}
