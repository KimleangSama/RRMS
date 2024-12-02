package com.kkimleang.rrms.payload.response.payment;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.room.*;
import com.kkimleang.rrms.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
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

    public static PaymentResponse fromPayment(User user, Payment payment) {
        NullOrDeletedEntityValidator.validate(payment, "Payment");
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


    public static List<PaymentResponse> fromPayments(User validUser, List<Payment> payments) {
        List<PaymentResponse> responses = new ArrayList<>();
        for (Payment payment : payments) {
            try {
                responses.add(fromPayment(validUser, payment));
            } catch (Exception e) {
                log.debug("Failed to convert payment to response: {}", payment);
            }
        }
        return responses;
    }
}
