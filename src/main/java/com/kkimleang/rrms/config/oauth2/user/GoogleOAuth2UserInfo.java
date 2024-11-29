package com.kkimleang.rrms.config.oauth2.user;

import java.util.Map;

public final class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }
}
