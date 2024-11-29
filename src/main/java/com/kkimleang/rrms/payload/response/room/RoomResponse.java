package com.kkimleang.rrms.payload.response.room;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.response.file.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
public class RoomResponse {
    private UUID id;
    private String roomNumber;
    private Integer roomFloor;
    private String roomType;
    private String roomSize;
    private Double rentalPrice;
    private String availableStatus;
    private String availableDate;
    private String propertyId;
    private Boolean hasPrivilege = false;
    private Set<FileResponse> roomPictures;

    private static final String DELETION_LOG = "Room {} was deleted at {}";

    public static RoomResponse fromRoom(Room room) {
        validateRoom(room);
        return mapToResponse(room, false);
    }

    public static RoomResponse fromRoom(User user, Room room) {
        validateRoom(room);
        boolean hasPrivilege = user.getId().equals(room.getProperty().getUser().getId());
        return mapToResponse(room, hasPrivilege);
    }

    public static List<RoomResponse> fromRooms(List<Room> rooms) {
        return processRooms(rooms, null);
    }

    public static List<RoomResponse> fromRooms(User user, List<Room> rooms) {
        return processRooms(rooms, user);
    }

    private static List<RoomResponse> processRooms(List<Room> rooms, User user) {
        return rooms.stream()
                .filter(Objects::nonNull)
                .map(room -> {
                    try {
                        return user == null ? fromRoom(room) : fromRoom(user, room);
                    } catch (ResourceDeletionException e) {
                        log.info(DELETION_LOG, room.getId(), room.getDeletedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static void validateRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (room.getDeletedBy() != null && room.getDeletedAt() != null) {
            throw new ResourceDeletionException("Room", room.getDeletedAt().toString());
        }
    }

    private static RoomResponse mapToResponse(Room room, boolean hasPrivilege) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomFloor(room.getRoomFloor());
        response.setRoomType(room.getRoomType().toString());
        response.setRoomSize(room.getRoomSize().toString());
        response.setRentalPrice(room.getRentalPrice());
        response.setAvailableStatus(room.getAvailableStatus().toString());
        response.setAvailableDate(room.getAvailableDate().toString());
        response.setPropertyId(room.getProperty().getId().toString());
        response.setHasPrivilege(hasPrivilege);
        response.setRoomPictures(FileResponse.fromPropRoomPictures(room.getRoomPictures()));
        return response;
    }
}