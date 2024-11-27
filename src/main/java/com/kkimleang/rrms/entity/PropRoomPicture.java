package com.kkimleang.rrms.entity;

import com.kkimleang.rrms.enums.file.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("PropertyPictures")
@Getter
@Setter
@ToString
@Entity
@Table(name = "prop_room_pictures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"filename"}, name = "unq_filename"),
})
public class PropRoomPicture extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "filename")
    private String filename;

    @NotNull
    @Column(name = "accessible_type")
    @Enumerated(EnumType.STRING)
    private FileAccessibleType fileAccessibleType = FileAccessibleType.PUBLIC;

    @NotNull
    @Column(name = "location_type")
    @Enumerated(EnumType.STRING)
    private FileLocationType location = FileLocationType.LOCAL;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prop_id")
    private Property property;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
}
