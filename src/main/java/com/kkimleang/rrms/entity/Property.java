package com.kkimleang.rrms.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kkimleang.rrms.enums.property.PropertyStatus;
import com.kkimleang.rrms.enums.property.PropertyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@RedisHash("Properties")
@Getter
@Setter
@ToString
@Entity
@Table(name = "properties", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"picture_cover"}, name = "unq_picture_cover"),
})
public class Property extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    private String description;

    @NotNull
    @Column(name = "picture_cover", nullable = false)
    private String pictureCover;

    @NotNull
    @Column(name = "address_proof", nullable = false)
    private String addressProof;

    private String village;
    private String commune;
    private String district;
    private String province;

    private String email;
    private String website;
    @NotNull
    private String contact;
    private String zipCode;
    @NotNull
    @Column(name = "address_gmap", nullable = false)
    private String addressGMap;
    private Double latitude;
    private Double longitude;

    @NotNull
    @Column(name = "property_status")
    @Enumerated(EnumType.STRING)
    private PropertyStatus propertyStatus = PropertyStatus.PENDING;

    @Column(name = "penalized_reason")
    private String penalizedReason;

    @NotNull
    @Column(name = "property_type")
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType = PropertyType.HOUSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<PropRoomPicture> propRoomPictures = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "properties_chars",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "char_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"property_id", "char_id"}, name = "unq_property_char")}
    )
    private Set<PropertyChars> propertyChars = new HashSet<>();
}
