package com.kkimleang.rrms.service.payment;

import com.kkimleang.rrms.entity.Invoice;
import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.entity.RoomAssignment;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.request.mapper.InvoiceMapper;
import com.kkimleang.rrms.payload.request.payment.CreateInvoiceRequest;
import com.kkimleang.rrms.payload.response.payment.InvoiceResponse;
import com.kkimleang.rrms.repository.payment.InvoiceRepository;
import com.kkimleang.rrms.service.room.RoomAssignmentService;
import com.kkimleang.rrms.service.room.RoomService;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import com.kkimleang.rrms.util.NullOrDeletedEntityValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final RoomAssignmentService roomAssignmentService;
    private final RoomService roomService;

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

    @Transactional
    public List<InvoiceResponse> getInvoicesOfRoom(CustomUserDetails user, UUID roomId, int page, int size) {
        NullOrDeletedEntityValidator.validate(user.getUser(), "User");
        RoomAssignment roomAssignment = roomAssignmentService.findByRoomId(roomId);
        NullOrDeletedEntityValidator.validate(roomAssignment, "Room Assignment");
        Page<Invoice> invoices = findAllByRoomAssignmentId(roomAssignment.getId(), page, size);
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

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
}
