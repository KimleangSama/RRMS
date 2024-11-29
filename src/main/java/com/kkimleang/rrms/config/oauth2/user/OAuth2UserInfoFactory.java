package com.kkimleang.rrms.config.oauth2.user;

import com.kkimleang.rrms.enums.user.AuthProvider;
import com.kkimleang.rrms.exception.OAuth2AuthenticationProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        log.info("attributes: {}", attributes);
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Sorry! Login with " + registrationId + " is not supported yet."
            );
        }
    }
}
