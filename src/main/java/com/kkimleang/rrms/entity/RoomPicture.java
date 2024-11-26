package com.kkimleang.rrms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.*;

import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("RoomPictures")
@Getter
@Setter
@ToString
@Entity
@Table(name = "room_pictures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"picture_url"}, name = "unq_picture_url"),
})
public class RoomPicture extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "picture_url")
    private String pictureURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
