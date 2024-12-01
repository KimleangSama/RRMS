package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.PaymentMethod;
import com.kkimleang.rrms.enums.room.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;

@RedisHash("Payments")
@Getter
@Setter
@ToString
@Entity
@Table(name = "payments")
public class Payment extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "expected_amount")
    private Double expectedAmount;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.BANK;

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
}
