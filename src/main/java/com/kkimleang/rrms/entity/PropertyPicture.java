package com.kkimleang.rrms.entity;

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
@Table(name = "property_pictures", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"picture_url"}, name = "unq_picture_url"),
})
public class PropertyPicture extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "picture_url")
    private String pictureUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}
