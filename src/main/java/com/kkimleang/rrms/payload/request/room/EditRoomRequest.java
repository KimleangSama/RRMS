package com.kkimleang.rrms.payload.request.room;

import com.kkimleang.rrms.enums.room.RoomSize;
import com.kkimleang.rrms.enums.room.RoomType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EditRoomRequest {
    private String roomNumber;
    private Integer roomFloor;
    private RoomType roomType;
    private RoomSize roomSize;
    private Double rentalPrice;
}
