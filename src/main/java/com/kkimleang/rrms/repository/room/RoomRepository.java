package com.kkimleang.rrms.repository.room;

import com.kkimleang.rrms.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    Page<Room> findByPropertyId(UUID propertyId, Pageable pageable);
    boolean existsByPropertyIdAndRoomNumber(UUID propertyId, String roomNumber);
}
