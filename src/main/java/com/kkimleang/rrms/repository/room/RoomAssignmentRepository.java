package com.kkimleang.rrms.repository.room;

import com.kkimleang.rrms.entity.RoomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, UUID> {
    Optional<RoomAssignment> findRoomAssignmentByUserId(UUID id);
}
