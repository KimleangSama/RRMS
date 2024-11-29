package com.kkimleang.rrms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntityAudit that)) return false;
        if (!super.equals(o)) return false;
        return createdBy.equals(that.createdBy) &&
                updatedBy.equals(that.updatedBy) &&
                createdAt.equals(that.createdAt) &&
                updatedAt.equals(that.updatedAt) &&
                Objects.equals(deletedAt, that.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                createdBy, updatedBy, createdAt, updatedAt, deletedAt);
    }

    @Override
    public String toString() {
        return "BaseEntityAudit{" + "createdBy='" + createdBy + ", updatedBy='" + updatedBy + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", deleteAt=" + deletedAt + "}" + super.toString();
    }
}
