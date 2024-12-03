package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.user.*;
import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Roles")
@Getter
@Setter
@ToString
@Entity
@Table(name = "roles", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "unq_role_name")})
public class Role implements Serializable {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    @Indexed
    private UUID id;

    @NotNull
    private String name;

    public boolean isValidRole() {
        for (AuthRole role : AuthRole.values()) {
            if (role.name().equals(this.name)) {
                return true;
            }
        }
        return false;
    }
}
