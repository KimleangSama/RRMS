package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;

@RedisHash("Invoice")
@Getter
@Setter
@ToString
@Entity
@Table(name = "invoices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_assignment_id", "invoice_date"}),
        @UniqueConstraint(columnNames = {"room_assignment_id", "due_date"}),
})
public class Invoice extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "amount_due", nullable = false)
    private Double amountDue;

    @Column(name = "discount", nullable = false)
    private Double discount;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "invoice_status", nullable = false)
    private InvoiceStatus invoiceStatus = InvoiceStatus.UNPAID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_assignment_id", nullable = false)
    private RoomAssignment roomAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
