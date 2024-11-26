package com.kkimleang.rrms.service.user;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.repository.user.*;
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission findByName(String name) {
        return permissionRepository.findByName(name);
    }
}
