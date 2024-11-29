package com.kkimleang.rrms.payload.request.user;

import com.kkimleang.rrms.enums.user.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class SignUpRequest {
    @NotBlank
    private String fullname;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    private Gender gender;
    @NotBlank
    private String password;
    private Set<String> roles;
}