package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.*;
import java.time.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Contracts")
@Getter
@Setter
@ToString
@Entity
@Table(name = "contracts")
public class Contract extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "signed_date", nullable = false)
    private LocalDateTime signedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @NotNull
    @Column(name = "contract_file_url", nullable = false)
    private String contractFileUrl;

    @NotNull
    @Column(name = "contract_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus = ContractStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_assignment_id", nullable = false)
    private RoomAssignment roomAssignment;
}
