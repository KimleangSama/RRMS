package com.kkimleang.rrms.payload.response.payment;

import com.fasterxml.jackson.annotation.*;
import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.room.*;
import com.kkimleang.rrms.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.modelmapper.*;

@Slf4j
@Getter
@Setter
@ToString
public class InvoiceResponse {
    private UUID id;
    private String roomNumber;

    private String tenantId;
    @JsonProperty("tenantFullName")
    private String fullname;
    private String phoneNumber;

    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private Double amountPaid;
    private Double amountDue;
    private Double discount;
    private Double totalAmount;
    private String remark;
    private InvoiceStatus invoiceStatus;
    private Boolean hasPrivilege = false;

    private static ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    private static InvoiceResponse mapToResponse(
            Invoice invoice,
            Boolean hasPrivilege
    ) {
        RoomAssignment roomAssignment = invoice.getRoomAssignment();
        NullOrDeletedEntityValidator.validate(roomAssignment, "Room Assignment");

        Room room = invoice.getRoomAssignment().getRoom();
        NullOrDeletedEntityValidator.validate(invoice.getRoomAssignment().getRoom(), "Room");

        User tenant = invoice.getRoomAssignment().getUser();
        NullOrDeletedEntityValidator.validate(tenant, "Tenant");

        InvoiceResponse response = new InvoiceResponse();
        modelMapper.map(invoice, response);
        response.setHasPrivilege(hasPrivilege);
        response.setId(invoice.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setTenantId(tenant.getId().toString());
        response.setFullname(tenant.getFullname());
        response.setPhoneNumber(tenant.getPhoneNumber());
        return response;
    }

    public static InvoiceResponse fromInvoice(User user, Invoice invoice) {
        boolean hasPrivilege = user.getId().equals(invoice.getCreatedBy());
        return mapToResponse(invoice, hasPrivilege);
    }

    public static List<InvoiceResponse> fromInvoices(User user, List<Invoice> invoices) {
        List<InvoiceResponse> responses = new ArrayList<>();
        for (Invoice invoice : invoices) {
            try {
                NullOrDeletedEntityValidator.validate(invoice, "Invoice");
                responses.add(fromInvoice(user, invoice));
            } catch (Exception e) {
                log.debug("Failed to map invoice to response: {}", e.getMessage());
            }
        }
        return responses;
    }
}
