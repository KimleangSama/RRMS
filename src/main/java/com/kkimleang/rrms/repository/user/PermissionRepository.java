package com.kkimleang.rrms.repository.user;

import com.kkimleang.rrms.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Permission findByName(String name);
}
