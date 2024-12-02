package com.kkimleang.rrms.payload.response.payment;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.room.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class PaymentResponse {
    private UUID id;

    private UUID tenantId;
    private String tenantFullname;

    private UUID invoiceId;
    private String roomNumber;
    private LocalDateTime paymentDate;

    private Double expectedAmount;
    private Double amountPaid;
    private Double amountDue;
    private PaymentMethod paymentMethod;

    public static PaymentResponse fromPayment(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setTenantId(payment.getInvoice().getRoomAssignment().getUser().getId());
        response.setTenantFullname(payment.getInvoice().getRoomAssignment().getUser().getFullname());
        response.setInvoiceId(payment.getInvoice().getId());
        response.setRoomNumber(payment.getInvoice().getRoomAssignment().getRoom().getRoomNumber());
        response.setPaymentDate(payment.getPaymentDate());
        response.setExpectedAmount(payment.getInvoice().getAmountDue() + payment.getAmountPaid());
        response.setAmountPaid(payment.getAmountPaid());
        response.setAmountDue(payment.getInvoice().getAmountDue());
        response.setPaymentMethod(payment.getPaymentMethod());
        return response;
    }
}
