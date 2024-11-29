package com.kkimleang.rrms.service.room;

import com.kkimleang.rrms.entity.PropRoomPicture;
import com.kkimleang.rrms.entity.Property;
import com.kkimleang.rrms.entity.Room;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.exception.ResourceDuplicationException;
import com.kkimleang.rrms.exception.ResourceForbiddenException;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.request.mapper.RoomMapper;
import com.kkimleang.rrms.payload.request.room.CreateRoomRequest;
import com.kkimleang.rrms.payload.request.room.EditAvailableRequest;
import com.kkimleang.rrms.payload.request.room.EditRoomRequest;
import com.kkimleang.rrms.payload.response.room.RoomResponse;
import com.kkimleang.rrms.repository.file.PropRoomPictureRepository;
import com.kkimleang.rrms.repository.property.PropertyRepository;
import com.kkimleang.rrms.repository.room.RoomRepository;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.kkimleang.rrms.constant.RoomLogErrorMessage.ROOM;
import static com.kkimleang.rrms.constant.RoomLogErrorMessage.ROOM_ALREADY_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;
    private final PropRoomPictureRepository propRoomPictureRepository;

    @Transactional
    public RoomResponse createRoom(CustomUserDetails user, CreateRoomRequest request) {
        validateUser(user, "create property");
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
        validatePrivilege(user, room, "delete");

        room.setDeletedBy(user.getUser().getId());
        room.setDeletedAt(Instant.now());
        return RoomResponse.fromRoom(roomRepository.save(room));
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
        validatePrivilege(user, room, "edit availability of");

        RoomMapper.editRoomAvailableFromEditAvailableRequest(room, request);
        updateRoomCommon(room, user);
        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    private void validateUser(CustomUserDetails user, String operation) {
        if (user == null || user.getUser() == null) {
            throw new ResourceForbiddenException("Unauthorized to " + operation, "No user details provided");
        }
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
}