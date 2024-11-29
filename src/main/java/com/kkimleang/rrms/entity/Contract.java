package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.room.ContractStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;

@RedisHash("Contracts")
@Getter
@Setter
@ToString
@Entity
@Table(name = "contracts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "user_id"}),
})
public class Contract extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
