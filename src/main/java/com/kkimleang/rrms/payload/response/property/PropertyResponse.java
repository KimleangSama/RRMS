package com.kkimleang.rrms.payload.response.property;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.response.file.*;
import com.kkimleang.rrms.util.*;
import java.io.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.modelmapper.*;

@Slf4j
@Data
public class PropertyResponse implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private String email;
    private String contact;
    private String website;
    private String pictureCover;
    private String addressProof;
    private String village;
    private String commune;
    private String district;
    private String province;
    private String zipCode;
    private String addressGMap;
    private Double latitude;
    private Double longitude;
    private String propertyStatus;
    private String propertyType;
    private Set<CharacteristicResponse> characteristics;
    private Set<FileResponse> propertyPictures;
    private UUID landlordId;
    private String landlordFullname;
    private String profilePicture;
    private Boolean hasPrivilege = false;

    private static final ModelMapper modelMapper = ModelMapperConfig.modelMapper();
    private static final String DELETION_LOG = "Property {} is deleted at {}";

    public static PropertyResponse fromProperty(User user, Property property) {
        DeletableEntityValidator.validate(property, "Property");
        PropertyResponse response = createPropertyResponse(property);
        if (user == null) return response;
        response.setHasPrivilege(user.getId().equals(property.getUser().getId()));
        return response;
    }

    public static List<PropertyResponse> fromProperties(User user, List<Property> properties) {
        return properties.stream()
                .map(property -> {
                    try {
                        return fromProperty(user, property);
                    } catch (ResourceDeletionException e) {
                        log.info(DELETION_LOG, property.getId(), property.getDeletedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static PropertyResponse createPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        ModelMapperConfig.modelMapper().map(property, response);
        User user = property.getUser();
        response.setLandlordId(user.getId());
        response.setLandlordFullname(user.getFullname());
        response.setProfilePicture(user.getProfilePicture());
        response.setCharacteristics(CharacteristicResponse.fromCharacteristics(property.getPropertyChars()));
        response.setPropertyPictures(FileResponse.fromPropRoomPictures(property.getPropRoomPictures()));
        return response;
    }
}