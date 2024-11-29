package com.kkimleang.rrms.controller.user;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.controller.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.user.*;
import com.kkimleang.rrms.payload.response.user.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

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
