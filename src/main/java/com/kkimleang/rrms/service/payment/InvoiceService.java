package com.kkimleang.rrms.service.payment;

import com.kkimleang.rrms.entity.Invoice;
import com.kkimleang.rrms.entity.RoomAssignment;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.request.mapper.InvoiceMapper;
import com.kkimleang.rrms.payload.request.payment.CreateInvoiceRequest;
import com.kkimleang.rrms.payload.response.payment.InvoiceResponse;
import com.kkimleang.rrms.repository.payment.InvoiceRepository;
import com.kkimleang.rrms.service.room.RoomAssignmentService;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import com.kkimleang.rrms.util.NullOrDeletedEntityValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final RoomAssignmentService roomAssignmentService;

    @Transactional
    public InvoiceResponse createInvoice(CustomUserDetails user, CreateInvoiceRequest request) {
        NullOrDeletedEntityValidator.validate(user.getUser(), "User");
        RoomAssignment roomAssignment = roomAssignmentService.findById(request.getRoomAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Assignment", request.getRoomAssignmentId()));
        Invoice invoice = new Invoice();
        invoice.setCreatedAt(Instant.now());
        invoice.setCreatedBy(user.getUser().getId());
        invoice.setRoomAssignment(roomAssignment);
        InvoiceMapper.createInvoiceFromInvoiceRequest(invoice, request);
        invoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromInvoice(user.getUser(), invoice);
    }
}
