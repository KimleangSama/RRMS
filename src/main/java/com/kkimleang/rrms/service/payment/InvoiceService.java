package com.kkimleang.rrms.service.payment;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.payment.*;
import com.kkimleang.rrms.payload.response.payment.*;
import com.kkimleang.rrms.repository.payment.*;
import com.kkimleang.rrms.service.room.*;
import com.kkimleang.rrms.service.user.*;
import com.kkimleang.rrms.util.*;
import jakarta.transaction.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final RoomAssignmentService roomAssignmentService;
    private final RoomService roomService;

    @Cacheable(value = "invoices")
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
        invoice.setTotalAmount(getTotalAmount(invoice));
        invoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromInvoice(user.getUser(), invoice);
    }

    @Cacheable(value = "invoices")
    @Transactional
    public List<InvoiceResponse> getInvoicesOfRoom(CustomUserDetails user, UUID roomId, int page, int size) {
        NullOrDeletedEntityValidator.validate(user.getUser(), "User");
        List<RoomAssignment> roomAssignments = roomAssignmentService.findByRoomId(roomId);
        NullOrDeletedEntityValidator.validate(roomAssignments, "Room Assignment");
        Page<Invoice> invoices = findAllByRoomAssignments(roomAssignments, page, size);
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

    @Cacheable(value = "invoices")
    @Transactional
    public List<InvoiceResponse> getInvoicesOfRoomAssignment(CustomUserDetails user, UUID roomAssignmentId, int page, int size) {
        NullOrDeletedEntityValidator.validate(user.getUser(), "User");
        RoomAssignment roomAssignment = roomAssignmentService.findById(roomAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Assignment", roomAssignmentId));
        NullOrDeletedEntityValidator.validate(roomAssignment, "Room Assignment");
        Page<Invoice> invoices = findAllByRoomAssignmentId(roomAssignment.getId(), page, size);
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

    private Page<Invoice> findAllByRoomAssignmentId(UUID roomAssignmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return invoiceRepository.findAllByRoomAssignmentId(roomAssignmentId, pageable);
    }

    @Transactional
    public List<InvoiceResponse> getInvoicesOfProperty(CustomUserDetails user, UUID propertyId, int page, int size) {
        NullOrDeletedEntityValidator.validate(user.getUser(), "User");
        List<Room> rooms = roomService.findRoomsByPropertyId(propertyId);
        List<RoomAssignment> roomAssignments = roomAssignmentService.findRoomAssignmentsByRooms(rooms);
        Page<Invoice> invoices = findAllByRoomAssignments(roomAssignments, page, size);
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

    private Page<Invoice> findAllByRoomAssignments(List<RoomAssignment> roomAssignments, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return invoiceRepository.findAllByRoomAssignmentIn(roomAssignments, pageable);
    }

    @Transactional
    @CachePut(value = "invoices")
    public InvoiceResponse editInvoiceStatus(CustomUserDetails user, UUID invoiceId, EditInvoiceStatusRequest request) {
        NullOrDeletedEntityValidator.validate(user.getUser(), "User");
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
        NullOrDeletedEntityValidator.validate(invoice, "Invoice");
        InvoiceMapper.editInvoiceStatusFromEditInvoiceStatusRequest(invoice, request);
        invoice.setUpdatedAt(Instant.now());
        invoice.setUpdatedBy(user.getUser().getId());
        invoice.setTotalAmount(getTotalAmount(invoice));
        invoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromInvoice(user.getUser(), invoice);
    }

    private double getTotalAmount(Invoice invoice) {
        if (invoice == null) {
            throw new ResourceNotFoundException("Invoice", "id");
        }
        double amountDue = Optional.ofNullable(invoice.getAmountDue())
                .orElseThrow(() -> new ResourceException("Invoice", "amount due"));
        double discount = Optional.ofNullable(invoice.getDiscount()).orElse(0.0);
        double amountPaid = Optional.ofNullable(invoice.getAmountPaid())
                .orElseThrow(() -> new ResourceException("Invoice", "amount paid"));
        double totalAmount = amountDue - discount - amountPaid;
        log.info("Calculated total amount: {}", totalAmount);
        return totalAmount;
    }

    @Cacheable(value = "invoices")
    @Transactional
    public Optional<Invoice> findById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }
}
