package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.BookingStatus;
import com.kkimleang.rrms.enums.room.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;

@RedisHash("Bookings")
@Getter
@Setter
@ToString
@Entity
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"booking_date", "room_id"}),
        @UniqueConstraint(columnNames = {"room_id", "user_id"}),
})
public class Booking extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date")
    private LocalDateTime checkOutDate;

    @Column(name = "booking_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Lob
    @Column(name = "remark")
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
