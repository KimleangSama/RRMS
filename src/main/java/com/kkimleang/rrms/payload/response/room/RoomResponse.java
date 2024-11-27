package com.kkimleang.rrms.payload.response.room;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
    private Set<String> roomPictures;

    private final static String INFO = "Room {} is deleted at {}";

    public static RoomResponse fromRoom(Room room) {
        if (room.getDeletedBy() == null || room.getDeletedAt() == null) {
            return mappingRoom(room);
        } else {
            throw new ResourceDeletedException("Room", room.getDeletedAt().toString());
        }
    }

    public static RoomResponse fromRoom(User user, Room room) {
        if (room.getDeletedBy() == null || room.getDeletedAt() == null) {
            RoomResponse response = mappingRoom(room);
            log.error("{}", room.getProperty().getUser().getId());
            if (user.getId().equals(room.getProperty().getUser().getId())) {
                response.setHasPrivilege(true);
            }
            return response;
        } else {
            throw new ResourceDeletedException("Room", room.getDeletedAt().toString());
        }
    }

    public static RoomResponse mappingRoom(Room room) {
        RoomResponse roomResponse = new RoomResponse();
        ModelMapperConfig.modelMapper().map(room, roomResponse);
        return roomResponse;
    }

    public static List<RoomResponse> fromRooms(List<Room> rooms) {
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            try {
                roomResponses.add(fromRoom(room));
            } catch (Exception e) {
                log.info(INFO, room.getId(), room.getDeletedAt());
            }
        }
        return roomResponses;
    }

    public static List<RoomResponse> fromRooms(User user, List<Room> rooms) {
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            try {
                roomResponses.add(fromRoom(user, room));
            } catch (Exception e) {
                log.info(INFO, room.getId(), room.getDeletedAt());
            }
        }
        return roomResponses;
    }
}
