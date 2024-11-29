package com.kkimleang.rrms.controller.room;

import com.kkimleang.rrms.annotation.CurrentUser;
import com.kkimleang.rrms.controller.GlobalControllerServiceCall;
import com.kkimleang.rrms.payload.Response;
import com.kkimleang.rrms.payload.request.room.CreateRoomRequest;
import com.kkimleang.rrms.payload.request.room.EditAvailableRequest;
import com.kkimleang.rrms.payload.request.room.EditRoomRequest;
import com.kkimleang.rrms.payload.response.room.RoomResponse;
import com.kkimleang.rrms.service.room.RoomService;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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