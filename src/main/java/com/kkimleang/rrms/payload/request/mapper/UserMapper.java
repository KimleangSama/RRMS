package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.payload.request.user.EditBasicRequest;
import com.kkimleang.rrms.payload.request.user.EditContactRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

@Slf4j
public class UserMapper {
    private UserMapper() {
    }

    private static final ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    public static void updateUserFromEditContactRequest(User entity, EditContactRequest contactRequest) {
        updateUser(entity, contactRequest, "contact");
    }

    public static void updateUserFromEditBasicRequest(User targetUser, EditBasicRequest request) {
        updateUser(targetUser, request, "basic");
    }

    private static <T> void updateUser(User user, T request, String requestType) {
        if (request == null) return;
        try {
            modelMapper.map(request, user);
        } catch (Exception e) {
            log.error("Failed to update user {} from edit {} request", user.getId(), requestType, e);
        }
    }
}