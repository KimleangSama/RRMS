package com.kkimleang.rrms.service.room;

import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.entity.RoomAssignment;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.room.AvailableStatus;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.exception.RoomNotAvailableException;
import com.kkimleang.rrms.exception.TenantAlreadyAssignedException;
import com.kkimleang.rrms.payload.request.mapper.RoomAssignmentMapper;
import com.kkimleang.rrms.payload.request.room.EditAvailableRequest;
import com.kkimleang.rrms.payload.request.room.RoomAssignmentRequest;
import com.kkimleang.rrms.payload.response.room.RoomAssignmentResponse;
import com.kkimleang.rrms.repository.room.RoomAssignmentRepository;
import com.kkimleang.rrms.repository.user.UserRepository;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.kkimleang.rrms.util.PrivilegeChecker.validateUser;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {
    private static final String CACHE_NAME = "room-assignment";
    private static final String RESOURCE_NAME = "Room Assignment";

    private final UserRepository userRepository;
    private final RoomService roomService;
    private final RoomAssignmentRepository roomAssignmentRepository;

    @Transactional
    public RoomAssignmentResponse assignRoom(CustomUserDetails user, RoomAssignmentRequest request) {
        validateUser(user, "create room assignment");
        // Find and validate tenant
        User tenant = findTenantByAssignmentCode(request.getAssignmentCode());
        validateTenantNotAssigned(tenant.getId());
        // Find and validate room
        Room room = roomService.findByRoomId(request.getRoomId());
        validateRoomAvailability(room);
        // Create and save assignment
        RoomAssignment assignment = createRoomAssignment(request, room, tenant);
        updateRoomStatus(user, room.getId(), AvailableStatus.ASSIGNED);
        return RoomAssignmentResponse.fromRoomAssignment(tenant, assignment);
    }

    @CacheEvict(value = CACHE_NAME, key = "#id")
    @Transactional
    public RoomAssignmentResponse deleteRoomAssignment(CustomUserDetails user, UUID id) {
        validateUser(user, "delete room assignment");
        RoomAssignment assignment = findAssignmentById(id);
        roomAssignmentRepository.delete(assignment);
        updateRoomStatus(user, assignment.getRoom().getId(), AvailableStatus.AVAILABLE);
        return RoomAssignmentResponse.fromRoomAssignment(user.getUser(), assignment);
    }

    @Cacheable(value = CACHE_NAME, key = "#id")
    @Transactional
    public RoomAssignmentResponse getRoomAssignmentByTenantId(CustomUserDetails user, UUID id) {
        validateUser(user, "get room assignment by tenant id");
        RoomAssignment assignment = roomAssignmentRepository.findRoomAssignmentByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "tenant id " + id));
        return RoomAssignmentResponse.fromRoomAssignment(user.getUser(), assignment);
    }

    // Private helper methods
    private User findTenantByAssignmentCode(String assignmentCode) {
        return userRepository.findByAssignmentCode(assignmentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "assignment code " + assignmentCode));
    }

    private void validateTenantNotAssigned(UUID tenantId) {
        roomAssignmentRepository.findRoomAssignmentByUserId(tenantId)
                .ifPresent(assignment -> {
                    throw new TenantAlreadyAssignedException("Tenant is already assigned to room: " + assignment.getRoom().getId());
                });
    }

    private void validateRoomAvailability(Room room) {
        if (room.getAvailableStatus() == AvailableStatus.ASSIGNED) {
            throw new RoomNotAvailableException("room is already " + room.getAvailableStatus());
        }
    }

    private RoomAssignment createRoomAssignment(RoomAssignmentRequest request, Room room, User tenant) {
        RoomAssignment assignment = new RoomAssignment();
        RoomAssignmentMapper.createRAFromRARequest(assignment, request, room, tenant);
        return roomAssignmentRepository.save(assignment);
    }

    private void updateRoomStatus(CustomUserDetails user, UUID roomId, AvailableStatus status) {
        EditAvailableRequest request = new EditAvailableRequest();
        request.setAvailableStatus(status);
        request.setAvailableDate(null);
        roomService.editRoomAvailable(user, roomId, request);
    }

    private RoomAssignment findAssignmentById(UUID id) {
        return roomAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}