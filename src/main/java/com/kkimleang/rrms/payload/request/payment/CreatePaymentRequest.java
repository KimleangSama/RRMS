package com.kkimleang.rrms.payload.request.payment;

import com.kkimleang.rrms.enums.room.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreatePaymentRequest {
    private UUID invoiceId;
    private LocalDateTime paymentDate;
    private Double amountPaid;
    private PaymentMethod paymentMethod = PaymentMethod.BANK;
    private String remark;
}
