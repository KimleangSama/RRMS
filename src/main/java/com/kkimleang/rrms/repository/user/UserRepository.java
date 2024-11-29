package com.kkimleang.rrms.repository.user;

import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.user.AuthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    Optional<User> findByAssignmentCode(String assignmentCode);
}
