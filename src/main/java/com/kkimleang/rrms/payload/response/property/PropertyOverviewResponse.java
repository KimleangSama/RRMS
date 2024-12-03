package com.kkimleang.rrms.payload.response.property;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.util.*;
import java.io.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropertyOverviewResponse implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private String pictureCover;
    private String addressProof;
    private String addressGMap;
    private String propertyStatus;
    private String propertyType;
    private Set<CharacteristicResponse> characteristics;

    private Boolean hasPrivilege = false;

    public static PropertyOverviewResponse fromProperty(User user, Property property) {
        DeletableEntityValidator.validate(property, "Property");
        PropertyOverviewResponse propertyResponse = new PropertyOverviewResponse();
        ModelMapperConfig.modelMapper().map(property, propertyResponse);
        if (user != null && user.getId().equals(property.getUser().getId())) {
            propertyResponse.setHasPrivilege(true);
        }
        propertyResponse.setCharacteristics(
                CharacteristicResponse.fromCharacteristics(property.getPropertyChars())
        );
        return propertyResponse;
    }

    public static List<PropertyOverviewResponse> fromProperties(User user, List<Property> properties) {
        List<PropertyOverviewResponse> propertyResponses = new ArrayList<>();
        for (Property property : properties) {
            try {
                propertyResponses.add(fromProperty(user, property));
            } catch (Exception e) {
                log.debug("Property {} is deleted at {}", property.getId(), property.getDeletedAt());
            }
        }
        return propertyResponses;
    }

    public static PropertyOverviewResponse fromPropertyResponse(PropertyResponse property) {
        DeletableEntityValidator.validate(property, "Property");
        PropertyOverviewResponse response = new PropertyOverviewResponse();
        ModelMapperConfig.modelMapper().map(property, response);
        return response;
    }
}
