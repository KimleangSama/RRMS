package com.kkimleang.rrms.repository.room;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByPropertyId(UUID propertyId);
    Page<Room> findByPropertyId(UUID propertyId, Pageable pageable);
    boolean existsByPropertyIdAndRoomNumber(UUID propertyId, String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.availableStatus != 'ASSIGNED'")
    Page<Room> findWhereStatusIsNotAssigned(PageRequest of);
}
