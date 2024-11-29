package com.kkimleang.rrms.payload.response.property;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.Property;
import com.kkimleang.rrms.entity.User;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
            propertyResponses.add(fromProperty(user, property));
        }
        return propertyResponses;
    }

    public static PropertyOverviewResponse fromPropertyResponse(PropertyResponse property) {
        PropertyOverviewResponse response = new PropertyOverviewResponse();
        ModelMapperConfig.modelMapper().map(property, response);
        return response;
    }
}
