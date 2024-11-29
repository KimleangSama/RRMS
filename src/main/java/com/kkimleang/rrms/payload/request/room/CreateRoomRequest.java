package com.kkimleang.rrms.payload.request.room;

import com.kkimleang.rrms.enums.room.AvailableStatus;
import com.kkimleang.rrms.enums.room.RoomSize;
import com.kkimleang.rrms.enums.room.RoomType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateRoomRequest {
    private String roomNumber;
    private Integer roomFloor;
    private RoomType roomType = RoomType.DOUBLE;
    private RoomSize roomSize = RoomSize.FOUR_BY_FOUR;
    private Double rentalPrice;
    private AvailableStatus availableStatus = AvailableStatus.AVAILABLE;
    private LocalDate availableDate;
    private UUID propertyId;
    private Set<String> roomPictures;
}
