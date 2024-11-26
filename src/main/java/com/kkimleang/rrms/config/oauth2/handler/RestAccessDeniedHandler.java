package com.kkimleang.rrms.config.oauth2.handler;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import org.slf4j.*;
import org.springframework.security.access.*;
import org.springframework.security.web.access.*;
import org.springframework.stereotype.*;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        logger.error("Responding with access denied error. Message - {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"statusCode\": 403, \"success\": false,  \"status\": \"FORBIDDEN_ACCESS_DENIED\", \"errors\": \"User is not authorized or forbidden to access.\"}");
    }
}
