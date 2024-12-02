package com.kkimleang.rrms.repository.payment;

import com.kkimleang.rrms.entity.*;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Page<Invoice> findAllByRoomAssignmentId(UUID id, Pageable pageable);

    Page<Invoice> findAllByRoomAssignmentIn(List<RoomAssignment> roomAssignments, Pageable pageable);

    Page<Invoice> findAllByRoomAssignmentInAndInvoiceDateIsAfter(List<RoomAssignment> roomAssignments, LocalDateTime date, Pageable pageable);
}
