package com.kkimleang.rrms.payload.request.user;

import com.kkimleang.rrms.enums.user.*;
import lombok.*;

@Getter
@Setter
@ToString
public class EditBasicRequest {
    private String fullname;
    private Gender gender;
    private String dateOfBirth;
    private String profilePicture;
    private String preferredLanguage;
    private String preferredLocation;
}