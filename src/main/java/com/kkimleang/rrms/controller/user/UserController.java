package com.kkimleang.rrms.controller.user;

import com.kkimleang.rrms.annotation.CurrentUser;
import com.kkimleang.rrms.controller.GlobalControllerServiceCall;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.Response;
import com.kkimleang.rrms.payload.request.user.EditBasicRequest;
import com.kkimleang.rrms.payload.request.user.EditContactRequest;
import com.kkimleang.rrms.payload.response.user.UserResponse;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import com.kkimleang.rrms.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/user")
public class UserController {
    private final UserService userService;
    private final GlobalControllerServiceCall service;

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER') or hasRole('EDITOR')")
    public Response<UserResponse> getCurrentUser(@CurrentUser CustomUserDetails currentUser) {
        return service.executeServiceCall(() -> {
            User user = userService.findByEmail(currentUser.getEmail());
            if (user == null) {
                throw new ResourceNotFoundException("User", currentUser.getEmail());
            }
            log.info("User: {}", user);
            return UserResponse.fromUser(user);
        }, "Failed to fetch current user");
    }

    @PatchMapping("/{id}/edit-contact")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER') or hasRole('EDITOR')")
    public Response<UserResponse> editContact(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID targetId,
            @RequestBody EditContactRequest request) {
        return service.executeServiceCall(() -> {
            User editedUser = userService.editContactInformation(user, targetId, request);
            log.info("Edited user: {}", editedUser);
            return UserResponse.fromUser(editedUser);
        }, "Failed to edit contact information");
    }

    @PatchMapping("/{id}/edit-basic")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER') or hasRole('EDITOR')")
    public Response<UserResponse> editBasic(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID targetId,
            @RequestBody EditBasicRequest request) {
        return service.executeServiceCall(() -> {
            User editedUser = userService.editBasicInformation(user, targetId, request);
            log.info("Edited user: {}", editedUser);
            return UserResponse.fromUser(editedUser);
        }, "Failed to edit basic information");
    }
}
