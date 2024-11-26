package com.kkimleang.rrms.service.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.RoomMapper;
import com.kkimleang.rrms.payload.request.room.CreateRoomRequest;
import com.kkimleang.rrms.payload.response.room.RoomResponse;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.repository.room.RoomPictureRepository;
import com.kkimleang.rrms.repository.room.RoomRepository;
import com.kkimleang.rrms.service.user.*;

import java.util.*;

import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final String RESOURCE = "Room";

    private final RoomRepository roomRepository;
    private final RoomPictureRepository roomPictureRepository;
    private final PropertyRepository propertyRepository;

    @Transactional
    public RoomResponse createRoom(CustomUserDetails user, CreateRoomRequest request) {
        try {
            if (user == null || user.getUser() == null) {
                throw new ResourceForbiddenException("Unauthorized to create property", request);
            }
            User currentUser = user.getUser();
            if (roomRepository.existsByPropertyIdAndRoomNumber(request.getPropertyId(), request.getRoomNumber())) {
                throw new ResourceForbiddenException(RESOURCE + " with room number " + request.getRoomNumber() + " already exists in your property.", currentUser.getUsername());
            }
            Property property = propertyRepository.findById(request.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property", "Id", request.getPropertyId()));
            Set<RoomPicture> pictures = roomPictureRepository.findAllByPictureURLIn(request.getRoomPictures());
            Room room = new Room();
            RoomMapper.createRoomFromCreateRoomRequest(room, request);
            room.setCreatedBy(currentUser.getId());
            room.setProperty(property);
            room.setRoomPictures(pictures);
            room = roomRepository.save(room);
            return RoomResponse.fromRoom(room);
        } catch (ResourceForbiddenException e) {
            log.error("Failed to create property {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to create property {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public List<RoomResponse> getRoomsOfProperty(CustomUserDetails user, UUID propertyId) {
        try {
            List<Room> rooms = roomRepository.findByPropertyId(propertyId);
            return RoomResponse.fromRooms(rooms);
        } catch (Exception e) {
            throw new ResourceException("Room", e.getMessage());
        }
    }

    public RoomResponse getRoomById(CustomUserDetails user, UUID roomId) {
        try {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room", "Id", roomId));
            log.info(room.getRoomPictures().toString());
            return RoomResponse.fromRoom(room);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException("Room", e.getMessage());
        }
    }
}
