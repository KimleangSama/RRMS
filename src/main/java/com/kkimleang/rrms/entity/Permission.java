package com.kkimleang.rrms.entity;

import com.redis.om.spring.annotations.Indexed;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("Permissions")
@Slf4j
@Getter
@Setter
@ToString
@Entity
@Table(name = "permissions", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "unq_name")})
public class Permission implements Serializable, GrantedAuthority {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    @Indexed
    private UUID id;

    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
