package com.kkimleang.rrms.payload.request.payment;

import com.kkimleang.rrms.enums.room.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateInvoiceRequest {
    private UUID roomAssignmentId;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private Double amountPaid;
    private Double amountDue;
    private Double discount;
    private String remark;
    private InvoiceStatus invoiceStatus = InvoiceStatus.UNPAID;
}
