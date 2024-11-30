package com.kkimleang.rrms.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkimleang.rrms.entity.Role;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.user.AuthProvider;
import com.kkimleang.rrms.enums.user.AuthRole;
import com.kkimleang.rrms.enums.user.AuthStatus;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.request.mapper.UserMapper;
import com.kkimleang.rrms.payload.request.user.EditBasicRequest;
import com.kkimleang.rrms.payload.request.user.EditContactRequest;
import com.kkimleang.rrms.payload.request.user.LoginRequest;
import com.kkimleang.rrms.payload.request.user.SignUpRequest;
import com.kkimleang.rrms.payload.response.user.AuthResponse;
import com.kkimleang.rrms.repository.user.UserRepository;
import com.kkimleang.rrms.util.PrivilegeChecker;
import com.kkimleang.rrms.util.RandomString;
import com.kkimleang.rrms.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;
    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    private final String FAILED_GET_EXCEPTION = "Failed to get user {}";
    private final String FAILED_EDIT_EXCEPTION = "Failed to edit user {}";

    @Cacheable(value = "user", key = "#email")
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(this::filterIsDeleted).orElse(null);
    }

    public User findByEmailOrUsername(String email, String username) {
        Optional<User> user = userRepository.findByEmailOrUsername(email, username);
        return user.map(this::filterIsDeleted).orElse(null);
    }

    public User createUser(SignUpRequest signUpRequest) {
        try {
            User user = new User();
            user.setFullname(signUpRequest.getFullname());
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setGender(signUpRequest.getGender());
            user.setAssignmentCode(RandomString.make(6));
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            // Setup roles following the request, if empty role, set default role to ROLE_USER
            if (signUpRequest.getRoles().isEmpty()) {
                Role userRole = roleService.findByName(AuthRole.USER.name());
                user.getRoles().add(userRole);
            } else {
                signUpRequest.getRoles().forEach(role -> {
                    try {
                        List<Role> roles = roleService.findByNames(List.of(role));
                        user.getRoles().addAll(roles);
                    } catch (ResourceNotFoundException e) {
                        log.error("Role not found: {} with message: {}", role, e.getMessage(), e);
                    }
                });
            }
            user.setProvider(AuthProvider.LOCAL);
            user.setVerifyCode(UUID.randomUUID().toString());
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Cannot create user with email: {}", signUpRequest.getEmail(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public AuthResponse loginUser(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + loginRequest.getEmail() + " not found."));
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            if (authentication == null) {
                throw new BadCredentialsException("Username or password is incorrect.");
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            user.setLastLoginAt(Instant.now());
            user.setUpdatedBy(user.getId());
            user = userRepository.save(user);
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    user.getUsername(),
                    tokenProvider.getExpirationDateFromToken(accessToken)
            );
        } catch (Exception e) {
            String message = "Cannot authenticate user. Please check email and password.";
            log.error(message, e);
            throw new BadCredentialsException(message);
        }
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = tokenProvider.getUserEmailFromToken(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userEmail));
            if (tokenProvider.isTokenValid(refreshToken, user)) {
                var accessToken = tokenProvider.createAccessToken(user);
                var authResponse = new AuthResponse(
                        accessToken,
                        refreshToken,
                        user.getUsername(),
                        tokenProvider.getExpirationDateFromToken(accessToken)
                );
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Cacheable(value = "user", key = "#id")
    public Optional<User> findById(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", id));
            return Optional.of(filterIsDeleted(user));
        } catch (Exception e) {
            log.error(FAILED_GET_EXCEPTION, id, e);
            throw new RuntimeException("Error while finding user with id: " + id + " with message: " + e.getMessage());
        }
    }

    @CachePut(value = "user", key = "#user.email")
    @Transactional
    public User updateVerifyAndAuthStatus(User user, AuthStatus status) {
        try {
            Integer success = userRepository.updateVerifyAndAuthStatus(user.getId(), status, true);
            if (success == 1) {
                log.info("User with id: {} is verified: {}", user.getId(), status.name());
                userRepository.updateVerifyCode(user.getId(), null);
                return user;
            } else {
                throw new RuntimeException("Cannot update user verification with id: " + user.getId());
            }
        } catch (Exception e) {
            log.error("Cannot save user with id: {}", user.getId(), e);
            throw new RuntimeException("User with id: " + user.getId() + " cannot be saved.");
        }
    }

    public User findUserByVerifyCode(String verifyCode) {
        try {
            User user = userRepository.findByVerifyCode(verifyCode)
                    .orElseThrow(() -> new ResourceNotFoundException("User", verifyCode));
            return filterIsDeleted(user);
        } catch (Exception e) {
            log.error("Cannot verify user with activate code: {}", verifyCode);
            throw new ResourceNotFoundException("User", verifyCode);
        }
    }

    private User filterIsDeleted(User user) {
        if (user.getUserStatus().equals(AuthStatus.CLOSED)) {
            throw new ResourceNotFoundException("User", user.getId());
        }
        return user;
    }

    @CachePut(value = "user", key = "#user.email")
    @Transactional
    public User editContactInformation(CustomUserDetails user, UUID targetId, EditContactRequest request) {
        try {
            User targetUser = userRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", targetId));
            if (PrivilegeChecker.withoutRight(user.getUser(), targetId)) {
                throw new RuntimeException("You are not allowed to edit this user information.");
            }
            UserMapper.updateUserFromEditContactRequest(targetUser, request);
            targetUser.setUpdatedAt(Instant.now());
            targetUser.setUpdatedBy(user.getUser().getId());
            return userRepository.save(targetUser);
        } catch (ResourceNotFoundException e) {
            log.error(FAILED_GET_EXCEPTION, targetId, e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, targetId, e);
            throw new RuntimeException("Cannot edit user with id: " + targetId);
        }
    }

    @CachePut(value = "user", key = "#user.email")
    @Transactional
    public User editBasicInformation(CustomUserDetails user, UUID targetId, EditBasicRequest request) {
        try {
            User targetUser = userRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", targetId));
            if (PrivilegeChecker.withoutRight(user.getUser(), targetId)) {
                throw new RuntimeException("You are not allowed to edit this user information.");
            }
            UserMapper.updateUserFromEditBasicRequest(targetUser, request);
            targetUser.setUpdatedAt(Instant.now());
            targetUser.setUpdatedBy(user.getUser().getId());
            return userRepository.save(targetUser);
        } catch (ResourceNotFoundException e) {
            log.error(FAILED_GET_EXCEPTION, targetId, e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, targetId, e);
            throw new RuntimeException("Cannot edit user with id: " + targetId);
        }
    }
}
