package com.kkimleang.rrms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kkimleang.rrms.enums.user.AuthRole;
import com.redis.om.spring.annotations.Indexed;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
