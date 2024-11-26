package com.kkimleang.rrms.payload.request.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EditPropertyBasicRequest {
    private String name;
    private String description;
    private String pictureCover;
    private String addressProof;
    private String province;
    private String district;
    private String commune;
    private String village;
    private String zipCode;
    private String addressGMap;
    private Double latitude;
    private Double longitude;
    private String propertyStatus;
    private String propertyType;
}
