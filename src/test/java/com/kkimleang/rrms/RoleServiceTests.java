package com.kkimleang.rrms;

import com.kkimleang.rrms.entity.Role;
import com.kkimleang.rrms.service.user.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class RoleServiceTests {
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
        Assert.assertEquals(2, roles.size());
    }
}
