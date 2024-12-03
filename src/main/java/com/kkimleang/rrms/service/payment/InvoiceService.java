package com.kkimleang.rrms.service.payment;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.payment.*;
import com.kkimleang.rrms.payload.response.payment.*;
import com.kkimleang.rrms.repository.payment.*;
import com.kkimleang.rrms.repository.property.*;
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
    private final RoomAssignmentService raService;
    private final RoomService roomService;
    private final PropertyRepository propertyRepository;

    @Transactional
    public InvoiceResponse createInvoice(CustomUserDetails user, CreateInvoiceRequest request) {
        validateUser(user);
        RoomAssignment roomAssignment = raService.findById(request.getRoomAssignmentId());
        DeletableEntityValidator.validate(roomAssignment.getRoom(), "Room");
        DeletableEntityValidator.validate(roomAssignment.getRoom().getProperty(), "Property");
        Invoice invoice = new Invoice();
        invoice.setCreatedBy(user.getUser().getId());
        invoice.setRoomAssignment(roomAssignment);
        InvoiceMapper.createInvoiceFromInvoiceRequest(invoice, request);
        invoice.setTotalAmount(calculateTotalAmount(invoice));
        invoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromInvoice(user.getUser(), invoice);
    }

    @Transactional
    public List<InvoiceResponse> getInvoicesOfRoom(CustomUserDetails user, UUID roomId, int page, int size) {
        validateUser(user);
        Room room = validateAndGetRoom(roomId);
        List<RoomAssignment> roomAssignments = validateAndGetRoomAssignments(roomId);
        validateRoomAccess(user, room, roomId);
        Page<Invoice> invoices = findAllByRoomAssignments(roomAssignments, PageRequest.of(page, size));
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

    /**
     * Retrieves paginated invoices for a room assignment.
     *
     * @param user             Authenticated user details
     * @param roomAssignmentId Room assignment identifier
     * @param page             Page number
     * @param size             Page size
     * @return List of invoice responses
     * @throws ResourceForbiddenException if user lacks access rights
     * @throws ResourceNotFoundException  if room assignment not found
     */
    @Transactional
    public List<InvoiceResponse> getInvoicesOfRoomAssignment(CustomUserDetails user, UUID roomAssignmentId, int page, int size) {
        validateUser(user);
        RoomAssignment roomAssignment = validateAndGetRoomAssignment(roomAssignmentId);
        List<RoomAssignment> roomAssignments = validateAndGetRoomAssignmentsForRoom(roomAssignment.getRoom().getId());
        validateUserPrivileges(user, roomAssignments, roomAssignmentId);
        RoomAssignment earliestAssignment = findEarliestValidAssignment(roomAssignments, roomAssignmentId);
        Page<Invoice> invoices = invoiceRepository.findAllByRoomAssignmentInAndInvoiceDateIsAfter(
                roomAssignments,
                earliestAssignment.getAssignmentDate(),
                PageRequest.of(page, size)
        );
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

    @Transactional
    public List<InvoiceResponse> getInvoicesOfProperty(CustomUserDetails user, UUID propertyId, int page, int size) {
        validateUser(user);
        Property property = validateAndGetProperty(propertyId);
        validatePropertyAccess(user, property, propertyId);
        List<Room> rooms = roomService.findRoomsByPropertyId(propertyId);
        List<RoomAssignment> roomAssignments = raService.findRoomAssignmentsByRooms(rooms);
        Page<Invoice> invoices = findAllByRoomAssignments(roomAssignments, PageRequest.of(page, size));
        return InvoiceResponse.fromInvoices(user.getUser(), invoices.getContent());
    }

    @CachePut(value = "invoices", key = "#invoiceId")
    @Transactional
    public InvoiceResponse editInvoiceStatus(CustomUserDetails user, UUID invoiceId, EditInvoiceInfoRequest request) {
        validateUser(user);
        Invoice invoice = validateAndGetInvoice(invoiceId);
        InvoiceMapper.editInvoiceStatusFromEditInvoiceStatusRequest(invoice, request);
        invoice.setUpdatedAt(Instant.now());
        invoice.setUpdatedBy(user.getUser().getId());
        invoice.setTotalAmount(calculateTotalAmount(invoice));
        return InvoiceResponse.fromInvoice(user.getUser(), invoiceRepository.save(invoice));
    }

    // Private helper methods
    private void validateUser(CustomUserDetails user) {
        DeletableEntityValidator.validate(user.getUser(), "User");
    }

    private Room validateAndGetRoom(UUID roomId) {
        Room room = roomService.findByRoomId(roomId);
        DeletableEntityValidator.validate(room, "Room");
        return room;
    }

    private List<RoomAssignment> validateAndGetRoomAssignments(UUID roomId) {
        List<RoomAssignment> assignments = raService.findByRoomId(roomId);
        DeletableEntityValidator.validate(assignments, "Room Assignment");
        return assignments;
    }

    private RoomAssignment validateAndGetRoomAssignment(UUID roomAssignmentId) {
        RoomAssignment assignment = raService.findById(roomAssignmentId);
        DeletableEntityValidator.validate(assignment, "Room Assignment");
        return assignment;
    }

    private List<RoomAssignment> validateAndGetRoomAssignmentsForRoom(UUID roomId) {
        List<RoomAssignment> assignments = raService.findByRoomId(roomId);
        DeletableEntityValidator.validate(assignments, "Room Assignment");
        return assignments;
    }

    private void validateUserPrivileges(CustomUserDetails user, List<RoomAssignment> roomAssignments, UUID resourceId) {
        boolean hasPrivilege = roomAssignments.stream()
                .anyMatch(ra ->
                        !PrivilegeChecker.isCreator(user.getUser(), ra.getCreatedBy()) ||
                                PrivilegeChecker.isRoomAssignmentTenant(user.getUser(), ra) ||
                                PrivilegeChecker.isRoomAssignmentOwner(user.getUser(), ra)
                );
        if (!hasPrivilege) {
            throw new ResourceForbiddenException("You are not allowed to access this resource", resourceId);
        }
    }

    private RoomAssignment findEarliestValidAssignment(List<RoomAssignment> assignments, UUID fallbackId) {
        return assignments.stream()
                .min(Comparator.comparing(RoomAssignment::getAssignmentDate))
                .orElseThrow(() -> new ResourceNotFoundException("Room Assignment", fallbackId));
    }

    private Property validateAndGetProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));
        DeletableEntityValidator.validate(property, "Property");
        return property;
    }

    private void validatePropertyAccess(CustomUserDetails user, Property property, UUID propertyId) {
        if (PrivilegeChecker.isCreator(user.getUser(), property.getCreatedBy()) ||
                PrivilegeChecker.isPropertyOwner(user.getUser(), property)) {
            return;
        }
        throw new ResourceForbiddenException("You are not allowed to access this resource", propertyId);
    }

    private void validateRoomAccess(CustomUserDetails user, Room room, UUID roomId) {
        if (PrivilegeChecker.isCreator(user.getUser(), room.getCreatedBy()) ||
                PrivilegeChecker.isRoomOwner(user.getUser(), room)) {
            return;
        }
        throw new ResourceForbiddenException("You are not allowed to access this resource", roomId);
    }

    private Invoice validateAndGetInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", invoiceId));
        DeletableEntityValidator.validate(invoice, "Invoice");
        return invoice;
    }

    private double calculateTotalAmount(Invoice invoice) {
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

    private Page<Invoice> findAllByRoomAssignments(List<RoomAssignment> roomAssignments, Pageable pageable) {
        return invoiceRepository.findAllByRoomAssignmentIn(roomAssignments, pageable);
    }
}
