package com.kkimleang.rrms.payload.response.user;

import com.kkimleang.rrms.entity.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RoleResponse implements Serializable {
    private UUID id;
    private String name;
    private Boolean isRoleValid;

    public static Set<RoleResponse> fromRoles(Set<Role> roles) {
        return roles.stream().map(role -> {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(role.getId());
            roleResponse.setName(role.getName());
            roleResponse.setIsRoleValid(role.isValidRole());
            return roleResponse;
        }).collect(java.util.stream.Collectors.toSet());
    }
}
