package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.*;
import jakarta.persistence.*;
import java.io.*;
import java.time.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Invoice")
@Getter
@Setter
@ToString
@Entity
@Table(name = "invoices")
public class Invoice extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "amount_paid")
    private Double amountPaid;

    @Column(name = "amount_due", nullable = false)
    private Double amountDue;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Lob
    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "invoice_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus = InvoiceStatus.UNPAID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_assignment_id", nullable = false)
    private RoomAssignment roomAssignment;
}
