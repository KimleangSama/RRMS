package com.kkimleang.rrms.entity;

import com.fasterxml.jackson.annotation.*;
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
@Table(name = "roles", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "unq_name")})
public class Role implements Serializable {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    @Indexed
    private UUID id;

    @NotNull
    private String name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"role_id", "permission_id"}, name = "unq_role_permission")}
    )
    private Set<Permission> permissions = new HashSet<>();

    public boolean isValidRole() {
        for (AuthRole role : AuthRole.values()) {
            if (role.name().equals(this.name)) {
                return true;
            }
        }
        return false;
    }
}
