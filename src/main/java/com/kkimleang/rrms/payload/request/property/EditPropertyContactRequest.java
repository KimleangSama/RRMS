package com.kkimleang.rrms.payload.request.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EditPropertyContactRequest {
    private String email;
    private String contact;
    private String website;
}
