package com.kkimleang.rrms.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {
    private String[] authorizedRedirectUris;
}