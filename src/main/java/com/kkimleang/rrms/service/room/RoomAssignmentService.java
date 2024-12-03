package com.kkimleang.rrms.service.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.room.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.room.*;
import com.kkimleang.rrms.payload.response.room.*;
import com.kkimleang.rrms.repository.room.*;
import com.kkimleang.rrms.repository.user.*;
import com.kkimleang.rrms.service.user.*;
import com.kkimleang.rrms.util.*;
import static com.kkimleang.rrms.util.PrivilegeChecker.*;
import jakarta.transaction.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {
    private static final String CACHE_NAME = "room-assignment";
    private static final String RESOURCE_NAME = "Room Assignment";

    private final UserRepository userRepository;
    private final RoomService roomService;
    private final RoomAssignmentRepository roomAssignmentRepository;

    @Transactional
    public RoomAssignmentResponse assignRoom(CustomUserDetails user, CreateRoomAssignmentRequest request) {
        validateUser(user, "create room assignment");
        // Find and validate tenant
        User tenant = findTenantByAssignmentCode(request.getAssignmentCode());
        validateTenantNotAssigned(tenant.getId());
        // Find and validate room
        Room room = roomService.findByRoomId(request.getRoomId());
        validateRoomAvailability(room);
        // Create and save assignment
        RoomAssignment assignment = createRoomAssignment(request, room, user.getUser().getId(), tenant);
        updateRoomStatus(user, room.getId(), AvailableStatus.ASSIGNED);
        return RoomAssignmentResponse.fromRoomAssignment(tenant, assignment);
    }

    @CacheEvict(value = CACHE_NAME, key = "#id")
    @Transactional
    public RoomAssignmentResponse deleteRoomAssignment(CustomUserDetails user, UUID id) {
        validateUser(user, "delete room assignment");
        RoomAssignment assignment = findAssignmentById(id);
        RoomAssignmentResponse response = RoomAssignmentResponse.fromRoomAssignment(user.getUser(), assignment);
        updateRoomStatus(user, assignment.getRoom().getId(), AvailableStatus.AVAILABLE);
        assignment.setDeletedBy(user.getUser().getId());
        assignment.setDeletedAt(Instant.now());
        assignment.setUser(null);
        roomAssignmentRepository.save(assignment);
        return response;
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
        User user = userRepository.findByAssignmentCode(assignmentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "assignment code " + assignmentCode));
        DeletableEntityValidator.validate(user, "Tenant");
        return user;
    }

    private void validateTenantNotAssigned(UUID tenantId) {
        roomAssignmentRepository.findRoomAssignmentByUserId(tenantId)
                .ifPresent(assignment -> DeletableEntityValidator.validate(assignment, "Room Assignment"));
    }

    private void validateRoomAvailability(Room room) {
        DeletableEntityValidator.validate(room.getProperty(), "Property");
        if (room.getAvailableStatus() != AvailableStatus.ASSIGNED && room.getAvailableStatus() != AvailableStatus.AVAILABLE) {
            throw new RoomNotAvailableException("room is already " + room.getAvailableStatus());
        }
    }

    private RoomAssignment createRoomAssignment(
            CreateRoomAssignmentRequest request,
            Room room,
            UUID createBy,
            User tenant
    ) {
        RoomAssignment assignment = new RoomAssignment();
        assignment.setCreatedBy(createBy);
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
        RoomAssignment ra = roomAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        DeletableEntityValidator.validate(ra.getRoom(), "Room Assignment");
        return ra;
    }

    @Transactional
    public List<RoomAssignmentResponse> getRoomAssignmentsByPropertyId(CustomUserDetails user, UUID id) {
        validateUser(user, "get room assignments by property id");
        List<Room> rooms = roomService.findRoomsByPropertyId(id);
        List<RoomAssignment> roomAssignments = roomAssignmentRepository.findRoomAssignmentsByRoomIn(rooms)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "property id " + id));
        return RoomAssignmentResponse.fromRoomAssignments(user.getUser(), roomAssignments);
    }

    public List<RoomAssignment> findByRoomId(UUID roomId) {
        return roomAssignmentRepository.findRoomAssignmentByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "room id " + roomId))
                .stream()
                .filter(assignment -> assignment.getDeletedBy() == null || assignment.getDeletedAt() == null)
                .toList();
    }

    public List<RoomAssignment> findRoomAssignmentsByRooms(List<Room> rooms) {
        return roomAssignmentRepository.findRoomAssignmentsByRoomIn(rooms)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "rooms"))
                .stream()
                .filter(assignment -> assignment.getDeletedBy() == null || assignment.getDeletedAt() == null)
                .toList();
    }

    public RoomAssignment findById(UUID id) {
        RoomAssignment ra = roomAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        DeletableEntityValidator.validate(ra.getRoom(), "Room Assignment");
        return ra;
    }
}