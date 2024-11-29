package com.kkimleang.rrms.config.filter;

import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.repository.user.UserRepository;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import com.kkimleang.rrms.util.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String email;
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && tokenProvider.isTokenNotExpired(jwt)) {
                email = tokenProvider.getUserEmailFromToken(jwt);
                User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
                if (tokenProvider.isTokenValid(jwt, user)) {
                    CustomUserDetails customUserDetails = new CustomUserDetails(user, null);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            customUserDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
