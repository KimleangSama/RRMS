package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.*;
import jakarta.persistence.*;
import java.io.*;
import java.time.*;
import lombok.*;
import org.springframework.data.redis.core.*;

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

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.BANK;

    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
}
