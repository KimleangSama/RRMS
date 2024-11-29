package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.request.room.*;
import lombok.extern.slf4j.*;
import org.modelmapper.*;

@Slf4j
public class RoomAssignmentMapper {
    private static final ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    public static void createRAFromRARequest(
            RoomAssignment assignment,
            RoomAssignmentRequest request,
            Room room, User tenant
    ) {
        modelMapper.map(request, assignment);
        assignment.setRoom(room);
        assignment.setUser(tenant);
    }
}
