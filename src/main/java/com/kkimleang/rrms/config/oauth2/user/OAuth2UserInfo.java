package com.kkimleang.rrms.config.oauth2.user;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public abstract class OAuth2UserInfo {
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
