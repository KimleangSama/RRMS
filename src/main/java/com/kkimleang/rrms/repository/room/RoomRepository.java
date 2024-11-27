package com.kkimleang.rrms.repository.room;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    Page<Room> findByPropertyId(UUID propertyId, Pageable pageable);
    boolean existsByPropertyIdAndRoomNumber(UUID propertyId, String roomNumber);
}
