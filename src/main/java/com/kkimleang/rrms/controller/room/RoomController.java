package com.kkimleang.rrms.controller.room;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.exception.*;
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
    private final RoomService roomService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<RoomResponse> createRoom(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateRoomRequest request
    ) {
        try {
            RoomResponse roomResponse = roomService.createRoom(user, request);
            return Response.<RoomResponse>ok()
                    .setPayload(roomResponse);
        } catch (Exception e) {
            log.error("Failed to create room {}", e.getMessage(), e);
            return Response.<RoomResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Response<List<RoomResponse>> getAllRooms(
            @CurrentUser CustomUserDetails user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            List<RoomResponse> roomResponses = roomService.getPagingRooms(user, page, size);
            return Response.<List<RoomResponse>>ok()
                    .setPayload(roomResponses);
        } catch (ResourceNotFoundException e) {
            return Response.<List<RoomResponse>>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<List<RoomResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/of-property/{propertyId}")
    public Response<List<RoomResponse>> getRoomsOfProperty(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID propertyId
    ) {
        try {
            List<RoomResponse> roomResponses = roomService.getRoomsOfProperty(user, propertyId);
            return Response.<List<RoomResponse>>ok()
                    .setPayload(roomResponses);
        } catch (ResourceNotFoundException e) {
            return Response.<List<RoomResponse>>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<List<RoomResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/{roomId}/view")
    public Response<RoomResponse> getRoomById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID roomId
    ) {
        try {
            RoomResponse roomResponse = roomService.getRoomById(user, roomId);
            return Response.<RoomResponse>ok()
                    .setPayload(roomResponse);
        } catch (ResourceNotFoundException e) {
            return Response.<RoomResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<RoomResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @DeleteMapping("/{roomId}/delete")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<RoomResponse> deleteRoomById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID roomId
    ) {
        try {
            RoomResponse roomResponse = roomService.deleteRoomById(user, roomId);
            return Response.<RoomResponse>ok()
                    .setPayload(roomResponse);
        } catch (ResourceNotFoundException e) {
            return Response.<RoomResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete room {}", e.getMessage(), e);
            return Response.<RoomResponse>exception()
                    .setErrors(e.getMessage());
        }
    }
}
