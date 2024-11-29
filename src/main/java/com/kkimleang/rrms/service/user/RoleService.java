package com.kkimleang.rrms.service.user;

import com.kkimleang.rrms.entity.Role;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.repository.user.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role", name)
                );
    }

    @Transactional
    public List<Role> findByNames(List<String> names) {
        return roleRepository.findByNameIn(names);
    }
}
