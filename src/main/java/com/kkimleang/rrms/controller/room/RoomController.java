package com.kkimleang.rrms.controller.room;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.controller.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.room.*;
import com.kkimleang.rrms.payload.response.room.*;
import com.kkimleang.rrms.service.room.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/room")
@RequiredArgsConstructor
public class RoomController {
    private final GlobalControllerServiceCall service;
    private final RoomService roomService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<RoomResponse> createRoom(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateRoomRequest request
    ) {
        return service.executeServiceCall(() -> roomService.createRoom(user, request),
                "Failed to create room");
    }

    @GetMapping("/all")
    public Response<List<RoomResponse>> getAllRooms(
            @CurrentUser CustomUserDetails user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> roomService.getRooms(user, null, page, size),
                "Failed to get all room");
    }

    @GetMapping("/all/open")
    public Response<List<RoomResponse>> getAllAvailableRooms(
            @CurrentUser CustomUserDetails user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> roomService.getOpenRooms(user, page, size),
                "Failed to get all room");
    }

    @GetMapping("/of-property")
    public Response<List<RoomResponse>> getRoomsOfProperty(
            @CurrentUser CustomUserDetails user,
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> roomService.getRooms(user, propertyId, page, size),
                "Failed to get rooms of property");
    }

    @GetMapping("/{roomId}/view")
    public Response<RoomResponse> getRoomById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID roomId
    ) {
        return service.executeServiceCall(() -> roomService.getRoomById(user, roomId),
                "Failed to get room");
    }

    @DeleteMapping("/{roomId}/delete")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<RoomResponse> deleteRoomById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID roomId
    ) {
        return service.executeServiceCall(() -> roomService.deleteRoomById(user, roomId),
                "Failed to delete room");
    }

    @PutMapping("/{roomId}/edit-info")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<RoomResponse> editRoom(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID roomId,
            @RequestBody EditRoomRequest request
    ) {
        return service.executeServiceCall(() -> roomService.editRoomInfo(user, roomId, request),
                "Failed to edit room");
    }

    @PatchMapping("/{roomId}/edit-available")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<RoomResponse> editRoomAvailable(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID roomId,
            @RequestBody EditAvailableRequest request
    ) {
        return service.executeServiceCall(() -> roomService.editRoomAvailable(user, roomId, request),
                "Failed to edit room availability");
    }
}