package com.kkimleang.rrms.service.user;

import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.repository.user.*;
import jakarta.transaction.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role", "name", name)
                );
    }

    @Transactional
    public List<Role> findByNames(List<String> names) {
        return roleRepository.findByNameIn(names);
    }
}
