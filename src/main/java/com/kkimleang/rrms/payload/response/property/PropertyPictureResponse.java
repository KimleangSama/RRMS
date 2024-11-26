package com.kkimleang.rrms.payload.response.property;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.PropertyPicture;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PropertyPictureResponse {
    private String pictureURL;
    private UUID createdBy;
    private Instant createdAt;

    public static Set<PropertyPictureResponse> fromPropertyPictures(Set<PropertyPicture> propertyPictures) {
        Set<PropertyPictureResponse> response = new HashSet<>();
        propertyPictures.forEach(propertyPicture -> {
            PropertyPictureResponse pictureResponse = ModelMapperConfig.modelMapper().map(propertyPicture, PropertyPictureResponse.class);
            pictureResponse.setCreatedBy(propertyPicture.getCreatedBy());
            pictureResponse.setCreatedAt(propertyPicture.getCreatedAt());
            response.add(pictureResponse);
        });
        return response;
    }
}
