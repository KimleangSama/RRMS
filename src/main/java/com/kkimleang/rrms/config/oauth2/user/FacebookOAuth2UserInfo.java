package com.kkimleang.rrms.config.oauth2.user;

import java.util.Map;
import java.util.Optional;

public final class FacebookOAuth2UserInfo extends OAuth2UserInfo {
    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getProfilePicture() {
        return Optional.ofNullable(attributes)
                .map(attrs -> (Map<String, Object>) attrs.get("picture"))
                .map(pictureObj -> (Map<String, Object>) pictureObj.get("data"))
                .map(dataObj -> (String) dataObj.get("url"))
                .orElse(null);
    }
}