package com.kkimleang.rrms.payload.request.user;

import com.kkimleang.rrms.enums.user.*;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.*;

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