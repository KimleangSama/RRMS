package com.kkimleang.rrms.config.oauth2.user;

import java.util.*;
import lombok.*;

@Getter
@ToString
public abstract sealed class OAuth2UserInfo permits GoogleOAuth2UserInfo, FacebookOAuth2UserInfo {
    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return (String) attributes.get("id");
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getProfilePicture() {
        return (String) attributes.get("picture");
    }
}
