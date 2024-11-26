package com.kkimleang.rrms.payload.response.property;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.property.*;

import java.io.*;
import java.util.*;

import com.kkimleang.rrms.exception.ResourceDeletedException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    // User Information
    private UUID landlordId;
    private String landlordFullname;
    private String profilePicture;
    private Boolean hasPrivilege = false;

    public static PropertyResponse fromProperty(Property property) {
        if (property.getDeletedBy() == null && property.getDeletedAt() == null) {
            PropertyResponse propertyResponse = mappingProperty(property);
            propertyResponse.setLandlordId(property.getUser().getId());
            propertyResponse.setLandlordFullname(property.getUser().getFullname());
            propertyResponse.setProfilePicture(property.getUser().getProfilePicture());
            propertyResponse.setCharacteristics(
                    CharacteristicResponse.fromCharacteristics(property.getPropertyCharacteristics())
            );
            return propertyResponse;
        } else {
            throw new ResourceDeletedException("Property", property.getDeletedAt().toString());
        }
    }

    public static PropertyResponse fromProperty(User user, Property property) {
        PropertyResponse propertyResponse = fromProperty(property);
        if (user.getId().equals(property.getUser().getId())) {
            propertyResponse.setHasPrivilege(true);
        }
        return propertyResponse;
    }

    private final static String INFO = "Property {} is deleted at {}";

    public static List<PropertyResponse> fromProperties(List<Property> properties) {
        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            try {
                propertyResponses.add(fromProperty(property));
            } catch (ResourceDeletedException e) {
                log.info(INFO, property.getId(), property.getDeletedAt());
            }
        }
        return propertyResponses;
    }

    public static List<PropertyResponse> fromProperties(User user, List<Property> properties) {
        List<PropertyResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            try {
                propertyResponses.add(fromProperty(user, property));
            } catch (ResourceDeletedException e) {
                log.info(INFO, property.getId(), property.getDeletedAt());
            }
        }
        return propertyResponses;
    }

    private static PropertyResponse mappingProperty(Property property) {
        PropertyResponse propertyResponse = new PropertyResponse();
        ModelMapperConfig.modelMapper().map(property, propertyResponse);
        return propertyResponse;
    }
}
