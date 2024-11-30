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

import static com.kkimleang.rrms.util.PrivilegeChecker.*;

import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final RoomAssignmentRepository roomAssignmentRepository;

    @Transactional
    public RoomAssignmentResponse assignRoom(CustomUserDetails user, RoomAssignmentRequest request) {
        validateUser(user, "create room assignment");
        User tenant = userRepository.findByAssignmentCode(request.getAssignmentCode())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "assignment code" + request.getAssignmentCode()));
        roomAssignmentRepository.findRoomAssignmentByUserId(tenant.getId())
                .ifPresent(assignment -> {
                    throw new TenantAlreadyAssignedException("Tenant is already assigned to a room: " + assignment.getRoom().getId());
                });
        Room room = roomService.findByRoomId(request.getRoomId());
        if (room.getAvailableStatus().equals(AvailableStatus.ASSIGNED)) {
            throw new RoomNotAvailableException("room is already " + room.getAvailableStatus());
        } else {
            RoomAssignment assignment = new RoomAssignment();
            RoomAssignmentMapper.createRAFromRARequest(assignment, request, room, tenant);
            roomAssignmentRepository.save(assignment);
            EditAvailableRequest req = new EditAvailableRequest();
            req.setAvailableStatus(AvailableStatus.ASSIGNED);
            req.setAvailableDate(null);
            roomService.editRoomAvailable(user, room.getId(), req);
            return RoomAssignmentResponse.fromRoomAssignment(tenant, assignment);
        }
    }

    @CacheEvict(value = "room-assignment", key = "#id")
    @Transactional
    public RoomAssignmentResponse deleteRoomAssignment(CustomUserDetails user, UUID id) {
        validateUser(user, "delete room assignment");
        RoomAssignment assignment = roomAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room Assignment", id));
        Room room = roomService.findByRoomId(assignment.getRoom().getId());
        roomAssignmentRepository.delete(assignment);
        EditAvailableRequest req = new EditAvailableRequest();
        req.setAvailableStatus(AvailableStatus.AVAILABLE);
        roomService.editRoomAvailable(user, room.getId(), req);
        return RoomAssignmentResponse.fromRoomAssignment(user.getUser(), assignment);
    }

    @Cacheable(value = "room-assignment", key = "#id")
    @Transactional
    public RoomAssignmentResponse getRoomAssignmentByTenantId(CustomUserDetails user, UUID id) {
        validateUser(user, "get room assignment by tenant id");
        RoomAssignment assignment = roomAssignmentRepository.findRoomAssignmentByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room Assignment", "tenant id" + id));
        return RoomAssignmentResponse.fromRoomAssignment(user.getUser(), assignment);
    }
}
