package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.Property;
import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.enums.property.PropertyStatus;
import com.kkimleang.rrms.enums.property.PropertyType;
import com.kkimleang.rrms.payload.request.property.CreatePropertyRequest;
import com.kkimleang.rrms.payload.request.property.EditPropertyBasicRequest;
import com.kkimleang.rrms.payload.request.property.EditPropertyContactRequest;
import com.kkimleang.rrms.payload.request.room.CreateRoomRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Optional;

@Slf4j
public class RoomMapper {
    public static void createRoomFromCreateRoomRequest(Room room, CreateRoomRequest request) {
        try {
            if (request == null) {
                return;
            }
            ModelMapperConfig.modelMapper().map(request, room);
        } catch (Exception e) {
            log.error("Failed to create room from request {}", e.getMessage(), e);
        }
    }
}
