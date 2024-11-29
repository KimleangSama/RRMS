package com.kkimleang.rrms.service.user;

import com.fasterxml.jackson.databind.*;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.user.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.user.*;
import com.kkimleang.rrms.payload.response.user.*;
import com.kkimleang.rrms.repository.user.*;
import com.kkimleang.rrms.util.*;
import jakarta.servlet.http.*;
import jakarta.transaction.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

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
