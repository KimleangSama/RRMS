package com.kkimleang.rrms.service.room;

import static com.kkimleang.rrms.constant.RoomLogErrorMessage.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.room.*;
import com.kkimleang.rrms.payload.response.room.*;
import com.kkimleang.rrms.repository.file.*;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.repository.room.*;
import com.kkimleang.rrms.service.user.*;
import static com.kkimleang.rrms.util.PrivilegeChecker.*;
import jakarta.transaction.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final PropRoomPictureRepository propRoomPictureRepository;

    @Transactional
    public RoomResponse createRoom(CustomUserDetails user, CreateRoomRequest request) {
        validateUser(user, "create room");
        validateRoomNumber(request.getPropertyId(), request.getRoomNumber());
        Property property = getPropertyById(request.getPropertyId());
        Set<PropRoomPicture> pictures = propRoomPictureRepository.findByFilenameIn(request.getRoomPictures());
        Room room = new Room();
        room.setCreatedBy(user.getUser().getId());
        room.setProperty(property);
        room.setRoomPictures(pictures);
        RoomMapper.createRoomFromCreateRoomRequest(room, request);
        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    @Transactional
    public List<RoomResponse> getRooms(CustomUserDetails user, UUID propertyId, int page, int size) {
        Page<Room> rooms = propertyId != null ?
                getRoomsForProperty(propertyId, page, size) :
                roomRepository.findAll(PageRequest.of(page, size));
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException(ROOM, "No rooms found");
        }
        return convertRoomListToRoomResponses(user, rooms.getContent());
    }

    @Transactional
    public List<RoomResponse> getOpenRooms(CustomUserDetails user, int page, int size) {
        Page<Room> rooms = roomRepository.findWhereStatusIsNotAssigned(PageRequest.of(page, size));
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException(ROOM, "No rooms found");
        }
        return convertRoomListToRoomResponses(user, rooms.getContent());
    }

    private List<RoomResponse> convertRoomListToRoomResponses(CustomUserDetails user, List<Room> rooms) {
        return Optional.ofNullable(user)
                .map(CustomUserDetails::getUser)
                .map(u -> RoomResponse.fromRooms(u, rooms))
                .orElseGet(() -> RoomResponse.fromRooms(rooms));
    }

    private Page<Room> getRoomsForProperty(UUID propertyId, int page, int size) {
        Property property = getPropertyById(propertyId);
        return roomRepository.findByPropertyId(property.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public RoomResponse getRoomById(CustomUserDetails user, UUID roomId) {
        Room room = getRoomById(roomId);
        RoomResponse response = RoomResponse.fromRoom(room);
        response.setHasPrivilege(hasPrivilege(user, room));
        return response;
    }

    @Transactional
    public RoomResponse deleteRoomById(CustomUserDetails user, UUID roomId) {
        validateUser(user, "delete room");
        Room room = getRoomById(roomId);
        RoomResponse response = RoomResponse.fromRoom(room);
        validatePrivilege(user, room, "delete");
        room.setDeletedBy(user.getUser().getId());
        room.setDeletedAt(Instant.now());
        roomRepository.save(room);
        return response;
    }

    @Transactional
    public RoomResponse editRoomInfo(CustomUserDetails user, UUID roomId, EditRoomRequest request) {
        validateUser(user, "edit room");
        Room room = getRoomById(roomId);
        validatePrivilege(user, room, "edit");

        RoomMapper.editRoomFromEditRoomRequest(room, request);
        updateRoomCommon(room, user);
        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse editRoomAvailable(CustomUserDetails user, UUID roomId, EditAvailableRequest request) {
        validateUser(user, "edit room available");
        Room room = getRoomById(roomId);
        RoomResponse response = RoomResponse.fromRoom(roomRepository.save(room));
        validatePrivilege(user, room, "edit availability of");
        RoomMapper.editRoomAvailableFromEditAvailableRequest(room, request);
        updateRoomCommon(room, user);
        roomRepository.save(room);
        return response;
    }

    private void validateRoomNumber(UUID propertyId, String roomNumber) {
        if (roomRepository.existsByPropertyIdAndRoomNumber(propertyId, roomNumber)) {
            throw new ResourceDuplicationException(ROOM_ALREADY_EXISTS,
                    String.format("%s and property %s", roomNumber, propertyId.toString()));
        }
    }

    private void validatePrivilege(CustomUserDetails user, Room room, String operation) {
        if (!hasPrivilege(user, room)) {
            throw new ResourceForbiddenException("Unauthorized to " + operation + " room", room);
        }
    }

    private boolean hasPrivilege(CustomUserDetails user, Room room) {
        try {
            return Optional
                    .ofNullable(user)
                    .map(CustomUserDetails::getUser)
                    .map(User::getId)
                    .map(userId -> room.getProperty().getUser().getId().equals(userId)).orElse(false);
        } catch (Exception e) {
            log.error("Failed to check privilege for room: {}", e.getMessage(), e);
            return false;
        }
    }

    private Property getPropertyById(UUID propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", propertyId.toString()));
    }

    private Room getRoomById(UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
    }

    private void updateRoomCommon(Room room, CustomUserDetails user) {
        room.setUpdatedBy(user.getUser().getId());
        room.setUpdatedAt(Instant.now());
    }

    @Cacheable(value = "room", key = "#roomId")
    public Room findByRoomId(UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
    }

    public List<Room> findRoomsByPropertyId(UUID id) {
        return roomRepository.findByPropertyId(id);
    }
}