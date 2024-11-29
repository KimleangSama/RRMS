package com.kkimleang.rrms.controller.user;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.user.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.user.*;
import com.kkimleang.rrms.payload.response.user.*;
import com.kkimleang.rrms.service.user.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.io.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;

    @PostMapping("/login")
    public Response<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = userService.loginUser(loginRequest);
            return Response.<AuthResponse>ok().setPayload(response);
        } catch (UsernameNotFoundException e) {
            return Response.<AuthResponse>wrongCredentials()
                    .setErrors("User with email " + loginRequest.getEmail() + " not found.")
                    .setPayload(null);
        } catch (BadCredentialsException e) {
            return Response.<AuthResponse>wrongCredentials()
                    .setErrors(e.getMessage())
                    .setPayload(null);
        } catch (Exception e) {
            return Response.<AuthResponse>exception()
                    .setErrors("User authentication failed. " + e.getMessage())
                    .setPayload(null);
        }
    }

    @PostMapping("/register")
    public Response<UserResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            User user = userService.findByEmailOrUsername(signUpRequest.getEmail(), signUpRequest.getUsername());
            if (user != null) {
                if (!user.getProvider().equals(AuthProvider.LOCAL)) {
                    return Response.<UserResponse>badRequest()
                            .setErrors("Look like you're already registered with " + user.getProvider() + " account. Please login with " + user.getProvider() + " account.");
                } else {
                    return Response.<UserResponse>badRequest()
                            .setErrors("User with email " + signUpRequest.getEmail() + " or username " + signUpRequest.getUsername() + " already exists.");
                }
            }
            user = userService.createUser(signUpRequest);
            return Response.<UserResponse>created().setPayload(UserResponse.fromUser(user));
        } catch (Exception e) {
            log.error("User registration failed. Reason: {}", e.getMessage(), e);
            return Response.<UserResponse>badRequest()
                    .setErrors("User registration failed. Reason: " + e.getMessage());
        }
    }

    @GetMapping("/check-roles")
    @PreAuthorize("authenticated")
    public Response<Set<RoleResponse>> getUserRoles(@CurrentUser CustomUserDetails currentUser) {
        try {
            User user = userService.findByEmail(currentUser.getEmail());
            Set<RoleResponse> roles = RoleResponse.fromRoles(user.getRoles(), true);
            return Response.<Set<RoleResponse>>ok()
                    .setPayload(roles);
        } catch (Exception e) {
            return Response.<Set<RoleResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public Response<UserResponse> verifyUser(
            @RequestParam("code") String verifyCode
    ) {
        try {
            User user = userService.findUserByVerifyCode(verifyCode);
            if (user != null) {
                user = userService.updateVerifyAndAuthStatus(user, AuthStatus.ACTIVE);
                return Response.<UserResponse>ok()
                        .setPayload(UserResponse.fromUser(user));
            } else {
                return Response.<UserResponse>badRequest()
                        .setErrors("Verification code may have already been used or expired.")
                        .setPayload(null);
            }
        } catch (ResourceNotFoundException e) {
            return Response.<UserResponse>exception()
                    .setErrors("Verification code may have already been used or expired.")
                    .setPayload(null);
        } catch (Exception e) {
            log.error("User verification failed. Reason: {}", e.getMessage(), e);
            return Response.<UserResponse>exception()
                    .setErrors("User verification failed. Reason: " + e.getMessage())
                    .setPayload(null);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.refreshToken(request, response);
    }
}