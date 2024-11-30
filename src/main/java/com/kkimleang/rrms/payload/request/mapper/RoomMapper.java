package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.request.room.*;
import lombok.extern.slf4j.*;
import org.modelmapper.*;

@Slf4j
public class RoomMapper {
    private static final ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    public static void createRoomFromCreateRoomRequest(Room room, CreateRoomRequest request) {
        mapRequest(room, request, "create");
    }

    public static void editRoomFromEditRoomRequest(Room room, EditRoomRequest request) {
        mapRequest(room, request, "edit");
    }

    public static void editRoomAvailableFromEditAvailableRequest(Room room, EditAvailableRequest request) {
        mapRequest(room, request, "edit available");
    }

    private static <T> void mapRequest(Room room, T request, String operation) {
        if (request == null) return;
        try {
            modelMapper.map(request, room);
        } catch (Exception e) {
            log.error("Failed to {} room: {}", operation, e.getMessage(), e);
        }
    }
}