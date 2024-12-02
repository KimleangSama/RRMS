package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.entity.RoomAssignment;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.payload.request.room.CreateRoomAssignmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

@Slf4j
public class RoomAssignmentMapper {
    private static final ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    public static void createRAFromRARequest(
            RoomAssignment assignment,
            CreateRoomAssignmentRequest request,
            Room room, User tenant
    ) {
        modelMapper.map(request, assignment);
        assignment.setRoom(room);
        assignment.setUser(tenant);
    }
}
