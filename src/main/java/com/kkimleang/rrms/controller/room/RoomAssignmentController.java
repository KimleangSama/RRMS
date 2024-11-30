package com.kkimleang.rrms.controller.room;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.controller.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.room.*;
import com.kkimleang.rrms.payload.response.room.*;
import com.kkimleang.rrms.service.room.*;
import com.kkimleang.rrms.service.user.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/room-assignment")
@RequiredArgsConstructor
public class RoomAssignmentController {
    private final GlobalControllerServiceCall service;
    private final RoomAssignmentService roomAssignmentService;

    @PostMapping("/assign")
//    @PreAuthorize("hasRole('LANDLOARD')")
    public Response<RoomAssignmentResponse> assignRoom(
            @CurrentUser CustomUserDetails user,
            @RequestBody RoomAssignmentRequest request
    ) {
        return service.executeServiceCall(() -> roomAssignmentService.assignRoom(user, request),
                "Failed to assign room to tenant");
    }

    @DeleteMapping("/{id}/delete")
//    @PreAuthorize("hasRole('LANDLOARD')")
    public Response<RoomAssignmentResponse> deleteRoomAssignment(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        return service.executeServiceCall(() -> roomAssignmentService.deleteRoomAssignment(user, id),
                "Failed to delete room assignment");
    }

    @GetMapping("/of-tenant/{id}")
    public Response<RoomAssignmentResponse> getRoomAssignmentByTenantId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        return service.executeServiceCall(() -> roomAssignmentService.getRoomAssignmentByTenantId(user, id),
                "Failed to get room assignment by tenant id");
    }
}
