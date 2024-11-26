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

import java.time.Instant;
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
    private final String FAILED_GET_EXCEPTION = "Failed to get room {} ";
    private final String FAILED_EDIT_EXCEPTION = "Failed to edit room {} ";

    private final RoomRepository roomRepository;
    private final RoomPictureRepository roomPictureRepository;
    private final PropertyRepository propertyRepository;

    private boolean withoutPrivilege(CustomUserDetails user, Room room) {
        try {
            log.info("User: {}", room.getProperty().getUser().getId());
            return user == null || user.getUser() == null || !room.getProperty().getUser().getId().equals(user.getUser().getId());
        } catch (Exception e) {
            log.error("Failed to check privilege for room {}", e.getMessage(), e);
            return true;
        }
    }

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
            Property property = propertyRepository.findById(request.getPropertyId()).orElseThrow(() -> new ResourceNotFoundException("Property", "Id", request.getPropertyId()));
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

    @Transactional
    public RoomResponse getRoomById(CustomUserDetails user, UUID roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "Id", roomId));
            log.info(room.getRoomPictures().toString());
            return RoomResponse.fromRoom(room);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException("Room", e.getMessage());
        }
    }

    @Transactional
    public RoomResponse deleteRoomById(CustomUserDetails user, UUID roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "Id", roomId));
            RoomResponse roomResponse = RoomResponse.fromRoom(room);
            if (withoutPrivilege(user, room)) {
                throw new ResourceForbiddenException("Unauthorized to delete room", room);
            }
            room.setDeletedBy(user.getUser().getId());
            room.setDeletedAt(Instant.now());
            room = roomRepository.save(room);
            log.info("Room After: {}", room.getDeletedAt());
            log.info("Room After: {}", room.getDeletedBy());
            return roomResponse;
        } catch (ResourceForbiddenException | ResourceNotFoundException | ResourceDeletedException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw new ResourceEditionException(FAILED_EDIT_EXCEPTION + e.getMessage());
        }
    }


}
