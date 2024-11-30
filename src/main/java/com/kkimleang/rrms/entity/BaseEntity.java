package com.kkimleang.rrms.entity;

import com.redis.om.spring.annotations.Indexed;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    @Indexed
    private UUID id;

    @Override
    public String toString() {
        return "BaseEntity {" +
                "id = " + id +
                "}";
    }
}