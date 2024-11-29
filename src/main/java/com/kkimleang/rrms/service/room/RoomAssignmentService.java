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
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class RoomAssignmentService {
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final RoomAssignmentRepository roomAssignmentRepository;

    public RoomAssignmentResponse assignRoom(CustomUserDetails user, RoomAssignmentRequest request) {
        validateUser(user, "create room assignment");
        Room room = roomService.findByRoomId(request.getRoomId());
        if (room.getAvailableStatus().equals(AvailableStatus.ASSIGNED)) {
            throw new RoomNotAvailableException("room is already " + room.getAvailableStatus());
        } else {
            User tenant = userRepository.findByAssignmentCode(request.getAssignmentCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.getAssignmentCode()));
            RoomAssignment assignment = new RoomAssignment();
            RoomAssignmentMapper.createRAFromRARequest(assignment, request, room, tenant);
            roomAssignmentRepository.save(assignment);
            EditAvailableRequest req = new EditAvailableRequest();
            req.setAvailableStatus(AvailableStatus.ASSIGNED);
            req.setAvailableDate(null);
            roomService.editRoomAvailable(user, room.getId(), req);
            return RoomAssignmentResponse.fromRoomAssignment(assignment);
        }
    }
}
