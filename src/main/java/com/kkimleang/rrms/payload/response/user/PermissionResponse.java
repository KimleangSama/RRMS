package com.kkimleang.rrms.payload.response.user;

import com.kkimleang.rrms.entity.*;
import java.io.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class PermissionResponse implements Serializable {
    private UUID id;
    private String name;

    public static Set<PermissionResponse> fromPermissions(Set<Permission> permissions) {
        return permissions.stream().map(permission -> {
            PermissionResponse permissionResponse = new PermissionResponse();
            permissionResponse.setId(permission.getId());
            permissionResponse.setName(permission.getName());
            return permissionResponse;
        }).collect(java.util.stream.Collectors.toSet());
    }
}
