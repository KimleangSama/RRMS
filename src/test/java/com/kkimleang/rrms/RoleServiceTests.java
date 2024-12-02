package com.kkimleang.rrms;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@Slf4j
@SpringBootTest
class RoleServiceTests {
    @Autowired
    private RoleService roleService;

    @Test
    void contextLoads() {
        log.info("Context loaded successfully");
    }

    @Test
    public void testFindByName() {
        Role role = roleService.findByName("ADMIN");
        log.info("Role: {}", role);
        Assertions.assertEquals("ADMIN", role.getName());
    }

    @Test
    public void testFindByNames() {
        List<Role> roles = roleService.findByNames(Arrays.asList("ADMIN", "SUPER_ADMIN"));
        log.info("Roles: {}", roles);
        Assertions.assertEquals(2, roles.size());
    }
}
