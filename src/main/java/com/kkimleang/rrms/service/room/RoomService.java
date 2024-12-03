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
import com.kkimleang.rrms.util.*;

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

    @CacheEvict(value = "rooms", allEntries = true)
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

    @Cacheable(value = "rooms")
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
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Room> rooms = roomRepository.findWhereStatusIsNotAssigned(pageable);
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

    @Cacheable(value = "rooms", key = "#roomId")
    @Transactional
    public RoomResponse getRoomById(CustomUserDetails user, UUID roomId) {
        Room room = getRoomById(roomId);
        RoomResponse response = RoomResponse.fromRoom(room);
        response.setHasPrivilege(PrivilegeChecker.isRoomOwner(user.getUser(), room));
        return response;
    }

    @CacheEvict(value = "rooms", key = "#roomId")
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

    @CachePut(value = "rooms", key = "#roomId")
    @Transactional
    public RoomResponse editRoomInfo(CustomUserDetails user, UUID roomId, EditRoomRequest request) {
        validateUser(user, "edit room");
        Room room = getRoomById(roomId);
        validatePrivilege(user, room, "edit");
        RoomMapper.editRoomFromEditRoomRequest(room, request);
        room.setUpdatedBy(user.getUser().getId());
        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    @CachePut(value = "rooms", key = "#roomId")
    @Transactional
    public RoomResponse editRoomAvailable(CustomUserDetails user, UUID roomId, EditAvailableRequest request) {
        validateUser(user, "edit room available");
        Room room = getRoomById(roomId);
        validatePrivilege(user, room, "edit availability of");
        RoomMapper.editRoomAvailableFromEditAvailableRequest(room, request);
        room.setUpdatedBy(user.getUser().getId());
        room = roomRepository.save(room);
        return RoomResponse.fromRoom(room);
    }

    private void validateRoomNumber(UUID propertyId, String roomNumber) {
        if (roomRepository.existsByPropertyIdAndRoomNumber(propertyId, roomNumber)) {
            throw new ResourceDuplicationException(ROOM_ALREADY_EXISTS,
                    String.format("%s and property %s", roomNumber, propertyId.toString()));
        }
    }

    private void validatePrivilege(CustomUserDetails user, Room room, String operation) {
        if (PrivilegeChecker.isCreator(user.getUser(), room.getCreatedBy()) ||
                PrivilegeChecker.isRoomOwner(user.getUser(), room)) {
            return;
        }
        throw new ResourceForbiddenException("Unauthorized to " + operation + " room", room);
    }

    private Property getPropertyById(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId.toString()));
        DeletableEntityValidator.validate(property, "Property");
        return property;
    }

    private Room getRoomById(UUID roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
        DeletableEntityValidator.validate(room, "Room");
        return room;
    }

    @Cacheable(value = "room", key = "#roomId")
    public Room findByRoomId(UUID roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
        DeletableEntityValidator.validate(room, "Room");
        return room;
    }

    public List<Room> findRoomsByPropertyId(UUID id) {
        return roomRepository.findByPropertyId(id).stream()
                .filter(r -> (r.getDeletedAt() == null || r.getDeletedBy() != null))
                .toList();
    }
}