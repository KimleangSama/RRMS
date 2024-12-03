package com.kkimleang.rrms.repository.payment;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
    Page<Contract> findByRoomAssignmentIn(List<RoomAssignment> roomAssignments, Pageable pageable);
}
