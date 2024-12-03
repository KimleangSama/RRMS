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
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final GlobalControllerServiceCall service;

    @GetMapping("/of-room/{roomId}")
//    @PreAuthorize("hasRole('LANDLORD')")
    public Response<List<InvoiceResponse>> getInvoicesOfRoom(
            @CurrentUser CustomUserDetails user,
            @PathVariable("roomId") UUID roomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> {
            List<InvoiceResponse> response = invoiceService.getInvoicesOfRoom(user, roomId, page, size);
            log.info("User {} fetched invoices of room {}", user.getUsername(), roomId);
            return response;
        }, "Failed to fetch invoices of room");
    }

    @GetMapping("/of-room-assignment/{roomAssignmentId}")
//    @PreAuthorize("hasRole('USER') or hasRole('LANDLORD')")
    public Response<List<InvoiceResponse>> getInvoicesOfRoomAssignment(
            @CurrentUser CustomUserDetails user,
            @PathVariable("roomAssignmentId") UUID roomAssignmentId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> {
            List<InvoiceResponse> response = invoiceService.getInvoicesOfRoomAssignment(user, roomAssignmentId, page, size);
            log.info("User {} fetched invoices of room assignment {}", user.getUsername(), roomAssignmentId);
            return response;
        }, "Failed to fetch invoices of room assignment");
    }

    @GetMapping("/of-property/{propertyId}")
//    @PreAuthorize("hasRole('LANDLORD')")
    public Response<List<InvoiceResponse>> getInvoicesOfProperty(
            @CurrentUser CustomUserDetails user,
            @PathVariable("propertyId") UUID propertyId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> {
            List<InvoiceResponse> response = invoiceService.getInvoicesOfProperty(user, propertyId, page, size);
            log.info("User {} fetched invoices of property {}", user.getUsername(), propertyId);
            return response;
        }, "Failed to fetch invoices of room assignment");
    }

    @PostMapping("/create")
//    @PreAuthorize("hasRole('LANDLORD')")
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

    @PatchMapping("/edit-status/{invoiceId}")
    public Response<InvoiceResponse> editInvoiceStatus(
            @CurrentUser CustomUserDetails user,
            @PathVariable("invoiceId") UUID invoiceId,
            @RequestBody EditInvoiceInfoRequest request
    ) {
        return service.executeServiceCall(() -> {
            InvoiceResponse response = invoiceService.editInvoiceStatus(user, invoiceId, request);
            log.info("User {} edited status of invoice {}", user.getUsername(), invoiceId);
            return response;
        }, "Failed to edit status of invoice");
    }
}
