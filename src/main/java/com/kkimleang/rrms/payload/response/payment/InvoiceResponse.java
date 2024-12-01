package com.kkimleang.rrms.payload.response.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.Invoice;
import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.entity.RoomAssignment;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.room.InvoiceStatus;
import com.kkimleang.rrms.exception.ResourceDeletionException;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.util.NullOrDeletedEntityValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.UUID;

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
}
