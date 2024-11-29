package com.kkimleang.rrms.payload.request.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EditContactRequest {
    private String email;
    private String phoneNumber;
    private String emergencyContact;
    private String emergencyRelationship;
    private String addressProof;
}