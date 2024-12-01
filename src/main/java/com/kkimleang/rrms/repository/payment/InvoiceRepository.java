package com.kkimleang.rrms.repository.payment;

import com.kkimleang.rrms.entity.Invoice;
import com.kkimleang.rrms.entity.Payment;
import com.kkimleang.rrms.entity.RoomAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Page<Invoice> findAllByRoomAssignmentId(UUID id, Pageable pageable);

    Page<Invoice> findAllByRoomAssignmentIn(List<RoomAssignment> roomAssignments, Pageable pageable);
}
