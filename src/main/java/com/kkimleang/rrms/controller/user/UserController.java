package com.kkimleang.rrms.controller.user;

import com.kkimleang.rrms.annotation.*;
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER') or hasRole('EDITOR')")
    public Response<UserResponse> getCurrentUser(@CurrentUser CustomUserDetails currentUser) {
        try {
            User user = userService.findByEmail(currentUser.getEmail());
            UserResponse userResponse = UserResponse.fromUser(user);
            log.info("UserResponse: {}", userResponse);
            return Response.<UserResponse>ok()
                    .setPayload(userResponse);
        } catch (Exception e) {
            return Response.<UserResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PatchMapping("/{id}/edit-contact")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER') or hasRole('EDITOR')")
    public Response<UserResponse> editContact(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID targetId,
            @RequestBody EditContactRequest request) {
        try {
            User updatedUser = userService.editContactInformation(user, targetId, request);
            UserResponse userResponse = UserResponse.fromUser(updatedUser);
            return Response.<UserResponse>ok()
                    .setPayload(userResponse);
        } catch (ResourceNotFoundException e) {
            return Response.<UserResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return Response.<UserResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PatchMapping("/{id}/edit-basic")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER') or hasRole('EDITOR')")
    public Response<UserResponse> editBasic(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID targetId,
            @RequestBody EditBasicRequest request) {
        try {
            User updatedUser = userService.editBasicInformation(user, targetId, request);
            UserResponse userResponse = UserResponse.fromUser(updatedUser);
            return Response.<UserResponse>ok()
                    .setPayload(userResponse);
        } catch (ResourceNotFoundException e) {
            return Response.<UserResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return Response.<UserResponse>exception()
                    .setErrors(e.getMessage());
        }
    }
}
