package com.kkimleang.rrms.service.user;

import com.kkimleang.rrms.entity.Permission;
import com.kkimleang.rrms.repository.user.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission findByName(String name) {
        return permissionRepository.findByName(name);
    }
}
