package com.kkimleang.rrms.repository.room;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, UUID> {
    Optional<RoomAssignment> findRoomAssignmentByUserId(UUID id);
}
