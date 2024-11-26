package com.kkimleang.rrms.payload.request.property;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreatePropertyRequest {
    private String name;
    private String email;
    private String contact;
    private String website;
    private String description;
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
    private String status;
    private String type;
    private Set<UUID> characteristics;
}
