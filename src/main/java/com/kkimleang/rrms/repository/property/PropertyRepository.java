package com.kkimleang.rrms.repository.property;

import com.kkimleang.rrms.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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
