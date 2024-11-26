package com.kkimleang.rrms.repository.user;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.user.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrUsername(String email, String username);

    List<User> findByIdIn(List<UUID> ids);

    Optional<User> findByVerifyCode(String verifyCode);

    @Modifying
    @Query("UPDATE User u SET u.userStatus = :status, u.verified = :verified WHERE u.id = :id")
    Integer updateVerifyAndAuthStatus(UUID id, AuthStatus status, Boolean verified);

    @Modifying
    @Query("UPDATE User u SET u.verifyCode = :nullValue WHERE u.id = :id")
    void updateVerifyCode(UUID id, Object nullValue);
}
