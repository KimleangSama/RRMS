package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.request.payment.*;
import java.time.*;
import lombok.extern.slf4j.*;

@Slf4j
public class PaymentMapper {

    public static void mapToPayment(User user, Payment payment, CreatePaymentRequest request) {
        ModelMapperConfig.modelMapper().map(request, payment);
        payment.setCreatedAt(Instant.now());
        payment.setCreatedBy(user.getId());
    }

    public static void mapToPayment(User user, Payment payment, EditPaymentInfoRequest request) {
        ModelMapperConfig.modelMapper().map(request, payment);
        payment.setUpdatedAt(Instant.now());
        payment.setUpdatedBy(user.getId());
    }
}
