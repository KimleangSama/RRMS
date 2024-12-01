package com.kkimleang.rrms.payload.response.user;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class TenantResponse {
    private UUID id;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String gender;
    private String profession;
    private String profilePicture;

    private final static ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    public static TenantResponse fromUserResponse(UserResponse userResponse) {
        TenantResponse tenantResponse = new TenantResponse();
        tenantResponse.setId(userResponse.getId());
        tenantResponse.setFullname(userResponse.getFullname());
        tenantResponse.setEmail(userResponse.getEmail());
        tenantResponse.setPhoneNumber(userResponse.getPhoneNumber());
        tenantResponse.setGender(userResponse.getGender());
        tenantResponse.setProfession(userResponse.getProfession());
        tenantResponse.setProfilePicture(userResponse.getProfilePicture());
        return tenantResponse;
    }

    public static List<TenantResponse> fromUserResponseList(List<UserResponse> userResponseList) {
        List<TenantResponse> tenantResponseList = new ArrayList<>();
        for (UserResponse userResponse : userResponseList) {
            tenantResponseList.add(fromUserResponse(userResponse));
        }
        return tenantResponseList;
    }

    public static TenantResponse fromUser(User user) {
        TenantResponse tenantResponse = modelMapper.map(user, TenantResponse.class);
        tenantResponse.setGender(user.getGender().toString());
        return tenantResponse;
    }
}
