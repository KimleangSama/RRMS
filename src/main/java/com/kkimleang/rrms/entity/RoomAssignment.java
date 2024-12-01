package com.kkimleang.rrms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDate;

@RedisHash("RoomAssignments")
@Getter
@Setter
@ToString
@Entity
@Table(name = "room_assignments")
public class RoomAssignment extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @Column(name = "expected_end_date")
    private LocalDate expectedEndDate;

    @Column(name = "rental_price", nullable = false)
    private Double rentalPrice; // Rental price confirmed by the user and the landlord

    @Lob
    @Column(name = "remark", nullable = false)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
