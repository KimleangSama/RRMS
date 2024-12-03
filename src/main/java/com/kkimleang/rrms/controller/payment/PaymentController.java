package com.kkimleang.rrms.controller.payment;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.controller.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.payment.*;
import com.kkimleang.rrms.payload.response.payment.*;
import com.kkimleang.rrms.service.payment.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final GlobalControllerServiceCall service;

    @PostMapping("/create")
    public Response<PaymentResponse> createPayment(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreatePaymentRequest request
    ) {
        return service.executeServiceCall(() -> paymentService.createPayment(user, request),
                "Failed to create payment");
    }

    @GetMapping("/of-invoice/{invoiceId}")
    public Response<List<PaymentResponse>> getPaymentOfInvoice(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID invoiceId
    ) {
        return service.executeServiceCall(() -> paymentService.getPaymentsOfInvoiceId(user, invoiceId),
                "Failed to get payment of invoice");
    }

    @PatchMapping("/{paymentId}/edit")
    public Response<PaymentResponse> editPayment(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID paymentId,
            @RequestBody EditPaymentInfoRequest request
    ) {
        return service.executeServiceCall(() -> paymentService.editPayment(user, paymentId, request),
                "Failed to edit payment");
    }
}
