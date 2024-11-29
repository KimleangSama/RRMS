package com.kkimleang.rrms.config.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"statusCode\": 403, \"success\": false,  \"status\": \"FORBIDDEN_ACCESS_DENIED\", \"errors\": \"User is not authorized or forbidden to access.\"}");
    }
}
