package com.kkimleang.rrms.controller.payment;

import com.kkimleang.rrms.annotation.CurrentUser;
import com.kkimleang.rrms.controller.GlobalControllerServiceCall;
import com.kkimleang.rrms.payload.Response;
import com.kkimleang.rrms.payload.request.payment.CreateInvoiceRequest;
import com.kkimleang.rrms.payload.response.payment.InvoiceResponse;
import com.kkimleang.rrms.service.payment.InvoiceService;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final GlobalControllerServiceCall service;

    @GetMapping("/of-property/{propertyId}")
    public Response<List<InvoiceResponse>> getInvoicesOfProperty(
            @CurrentUser CustomUserDetails user,
            @PathVariable("propertyId") UUID propertyId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return null;
    }

    @PostMapping("/create")
    public Response<InvoiceResponse> createInvoice(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateInvoiceRequest request
    ) {
        return service.executeServiceCall(() -> {
            InvoiceResponse response = invoiceService.createInvoice(user, request);
            log.info("User {} created invoice {}", user.getUsername(), response.getId());
            return response;
        }, "Failed to create invoice");
    }
}
