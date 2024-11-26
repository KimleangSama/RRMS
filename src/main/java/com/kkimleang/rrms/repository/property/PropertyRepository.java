package com.kkimleang.rrms.repository.property;

import com.kkimleang.rrms.entity.*;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    boolean existsByUserIdAndName(UUID id, String name);

    List<Property> findByUserId(UUID landlordId);

    @Query(value = """
                SELECT p.*,
                    (6371 * acos(
                        cos(radians(:userLat)) * cos(radians(p.latitude)) *
                        cos(radians(p.longitude) - radians(:userLng)) +
                        sin(radians(:userLat)) * sin(radians(p.latitude))
                    )) AS distance
                FROM properties p
                GROUP BY p.id
                HAVING (6371 * acos(
                        cos(radians(:userLat)) * cos(radians(p.latitude)) *
                        cos(radians(p.longitude) - radians(:userLng)) +
                        sin(radians(:userLat)) * sin(radians(p.latitude))
                    )) <= :radius
            """, nativeQuery = true)
    List<Property> findNearbyProperties(Double userLat, Double userLng, Double radius);
}
