package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.RentalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;

@RedisHash("RoomAssignments")
@Getter
@Setter
@ToString
@Entity
@Table(name = "room_assignments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assignment_date", "room_id"}),
        @UniqueConstraint(columnNames = {"room_id", "user_id"}),
})
public class RoomAssignment extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "assignment_date", nullable = false)
    private LocalDateTime assignmentDate;

    @Column(name = "expected_end_date", nullable = false)
    private LocalDateTime expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @Column(name = "rental_price", nullable = false)
    private Double rentalPrice;

    @Column(name = "rental_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalStatus rentalStatus;

    @Lob
    @Column(name = "remark", nullable = false)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
