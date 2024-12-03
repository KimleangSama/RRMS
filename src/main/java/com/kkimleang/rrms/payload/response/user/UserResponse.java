package com.kkimleang.rrms.payload.response.user;

import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.user.AuthProvider;
import com.kkimleang.rrms.enums.user.AuthStatus;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private UUID id;
    private String fullname;
    private String username;
    private String email;
    private String gender;
    private String phoneNumber;
    private String profession;
    private String profilePicture;
    private AuthProvider provider;
    private Set<RoleResponse> roles;
    private AuthStatus userStatus;
    private Instant lastLoginAt;

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setFullname(user.getFullname());
        userResponse.setGender(user.getGender().name());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setProfession(user.getProfession());
        userResponse.setProfilePicture(user.getProfilePicture());
        userResponse.setProvider(user.getProvider());
        userResponse.setRoles(RoleResponse.fromRoles(user.getRoles()));
        userResponse.setUserStatus(user.getUserStatus());
        userResponse.setLastLoginAt(user.getLastLoginAt());
        return userResponse;
    }

    public static List<UserResponse> fromUsers(List<User> users) {
        return users.stream()
                .map(UserResponse::fromUser)
                .toList();
    }
}
