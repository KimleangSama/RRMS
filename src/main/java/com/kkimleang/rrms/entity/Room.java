package com.kkimleang.rrms.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kkimleang.rrms.enums.room.AvailableStatus;
import com.kkimleang.rrms.enums.room.RoomSize;
import com.kkimleang.rrms.enums.room.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RedisHash("Rooms")
@Getter
@Setter
@ToString
@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_number", "property_id"}),
})
public class Room extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @NotNull
    @Column(name = "room_floor", nullable = false)
    private Integer roomFloor;

    @NotNull
    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType = RoomType.DOUBLE;

    @NotNull
    @Column(name = "room_size")
    @Enumerated(EnumType.STRING)
    private RoomSize roomSize = RoomSize.FOUR_BY_FOUR;

    @NotNull
    @Column(name = "rental_price", nullable = false)
    private Double rentalPrice;

    @NotNull
    @Column(name = "available_status")
    @Enumerated(EnumType.STRING)
    private AvailableStatus availableStatus = AvailableStatus.AVAILABLE;

    @NotNull
    @Column(name = "available_date")
    private LocalDate availableDate;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<PropRoomPicture> roomPictures = new HashSet<>();
}
