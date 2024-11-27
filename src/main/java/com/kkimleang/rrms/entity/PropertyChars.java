package com.kkimleang.rrms.entity;

import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("PropertyChars")
@Getter
@Setter
@ToString
@Entity
@Table(name = "property_chars", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class PropertyChars implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    @Indexed
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;
}
