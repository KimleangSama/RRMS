package com.kkimleang.rrms.service.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.room.*;
import com.kkimleang.rrms.payload.response.room.*;
import com.kkimleang.rrms.repository.file.*;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.repository.room.*;
import com.kkimleang.rrms.service.user.*;
import jakarta.transaction.*;

import java.time.*;
import java.util.*;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final String RESOURCE = "Room";
    private final String FAILED_CREATE_EXCEPTION = "Failed to create room {} ";
    private final String FAILED_GET_EXCEPTION = "Failed to get room {} ";
    private final String FAILED_EDIT_EXCEPTION = "Failed to edit room {} ";

    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final PropRoomPictureRepository propRoomPictureRepository;

    private boolean withoutPrivilege(CustomUserDetails user, Room room) {
        try {
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
            Set<PropRoomPicture> pictures = propRoomPictureRepository.findByFilenameIn(request.getRoomPictures());
            Room room = new Room();
            RoomMapper.createRoomFromCreateRoomRequest(room, request);
            room.setCreatedBy(currentUser.getId());
            room.setProperty(property);
            room.setRoomPictures(pictures);
            room = roomRepository.save(room);
            return RoomResponse.fromRoom(room);
        } catch (ResourceForbiddenException e) {
            log.error(FAILED_CREATE_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_CREATE_EXCEPTION, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<RoomResponse> getRoomsOfProperty(
            CustomUserDetails user,
            UUID propertyId,
            int page,
            int size
    ) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property", "Id", propertyId));
            Pageable pageable = PageRequest.of(page, size);
            Page<Room> rooms = roomRepository.findByPropertyId(property.getId(), pageable);
            if (rooms.isEmpty()) {
                throw new ResourceNotFoundException(RESOURCE, "of property " + propertyId, rooms);
            }
            List<Room> roomList = rooms.getContent();
            if (user == null || user.getUser() == null) {
                return RoomResponse.fromRooms(roomList);
            }
            return RoomResponse.fromRooms(user.getUser(), roomList);
        } catch (Exception e) {
            throw new ResourceException("Room", e.getMessage());
        }
    }

    @Transactional
    public RoomResponse getRoomById(CustomUserDetails user, UUID roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "Id", roomId));
            RoomResponse roomResponse = RoomResponse.fromRoom(room);
            if (!withoutPrivilege(user, room)) {
                roomResponse.setHasPrivilege(true);
            }
            return roomResponse;
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
            roomRepository.save(room);
            return roomResponse;
        } catch (ResourceForbiddenException | ResourceNotFoundException | ResourceDeletedException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw new ResourceEditionException(FAILED_EDIT_EXCEPTION + e.getMessage());
        }
    }

    @Transactional
    public List<RoomResponse> getPagingRooms(CustomUserDetails user, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Room> rooms = roomRepository.findAll(pageable);
            if (rooms.isEmpty()) {
                throw new ResourceNotFoundException(RESOURCE, "of size " + size + " at page " + page, rooms);
            }
            List<Room> roomList = rooms.getContent();
            if (user == null || user.getUser() == null) {
                return RoomResponse.fromRooms(roomList);
            }
            return RoomResponse.fromRooms(user.getUser(), roomList);
        } catch (Exception e) {
            throw new ResourceException("Room", e.getMessage());
        }
    }

    public RoomResponse editRoom(CustomUserDetails user, UUID roomId, EditRoomRequest request) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "Id", roomId));
            if (withoutPrivilege(user, room)) {
                throw new ResourceForbiddenException("Unauthorized to edit room", room);
            }
            RoomMapper.editRoomFromEditRoomRequest(room, request);
            room.setUpdatedBy(user.getUser().getId());
            room.setUpdatedAt(Instant.now());
            room = roomRepository.save(room);
            return RoomResponse.fromRoom(room);
        } catch (ResourceForbiddenException | ResourceNotFoundException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw new ResourceEditionException(FAILED_EDIT_EXCEPTION + e.getMessage());
        }
    }
}
