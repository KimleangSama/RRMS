package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.user.AuthProvider;
import com.kkimleang.rrms.enums.user.AuthStatus;
import com.kkimleang.rrms.enums.user.Gender;
import com.redis.om.spring.annotations.Indexed;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RedisHash("Users")
@Getter
@Setter
@ToString
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}, name = "unq_email"),
        @UniqueConstraint(columnNames = {"username"}, name = "unq_username"),
        @UniqueConstraint(columnNames = {"verify_code"}, name = "unq_verify_code"),
        @UniqueConstraint(columnNames = {"assignment_code"}, name = "unq_assignment_code"),
        @UniqueConstraint(columnNames = {"phone_number"}, name = "unq_phone_number"),
        @UniqueConstraint(columnNames = {"bank_account_number"}, name = "unq_bank_account_number"),
        @UniqueConstraint(columnNames = {"national_id"}, name = "unq_national_id")
})
public class User extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    private String fullname;
    @NotNull
    @Indexed
    private String username;
    private String password;

    @NotNull
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.NOT_SAID;

    @NotNull
    @Indexed
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "verify_code")
    private String verifyCode; // For email verification

    @Column(name = "assignment_code")
    private String assignmentCode; // Like secret for landlord to assign a room

    @Column(name = "verified")
    private Boolean verified;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_account_picture")
    private String bankAccountPicture;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "id_card_front")
    private String idCardFront;

    @Column(name = "id_card_back")
    private String idCardBack;

    @Column(name = "address_proof")
    private String addressProof;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Column(name = "preferred_location")
    private String preferredLocation;
    private Double preferredLatitude;
    private Double preferredLongitude;
    private Double preferredRadius = 5.0;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_relationship")
    private String emergencyRelationship;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @NotNull
    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    @NotNull
    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private AuthStatus userStatus = AuthStatus.PENDING;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"}, name = "unq_user_role")}
    )
    private Set<Role> roles = new HashSet<>();
}
