package com.kkimleang.rrms.config.security;

import com.kkimleang.rrms.config.filter.TokenAuthenticationFilter;
import com.kkimleang.rrms.config.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.kkimleang.rrms.config.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.kkimleang.rrms.config.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.kkimleang.rrms.config.oauth2.handler.RestAccessDeniedHandler;
import com.kkimleang.rrms.config.oauth2.handler.RestAuthenticationEntryPoint;
import com.kkimleang.rrms.config.oauth2.service.CustomOAuth2UserService;
import com.kkimleang.rrms.config.properties.CORSProperties;
import com.kkimleang.rrms.repository.user.UserRepository;
import com.kkimleang.rrms.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final CORSProperties corsProperties;
    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(userRepository, tokenProvider);
    }

    /*
     * By default, Spring OAuth2 uses
     * HttpSessionOAuth2AuthorizationRequestRepository to save
     * the authorization request. But, since our service is stateless, we can't save
     * it in the session. We'll save the request in a Base64 encoded cookie instead.
     */
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    private final String[] freeURLs = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/verify",

            "/api/v1/file/**",
            "/api/v1/property/**",
            "/api/v1/room/all",

            "/oauth2/**",
            "/error/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api-docs/**",
            "/aggregate/**",
            "/actuator/prometheus",
            "/actuator/health/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .anonymous(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .oauth2Login(oauth -> oauth.authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                        .redirectionEndpoint(red -> red.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(freeURLs)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/auth/logout")
                                .addLogoutHandler((request, response, auth) -> {
                                    final String header = request.getHeader(AUTHORIZATION);
                                    final String jwt;
                                    if (header == null || !header.startsWith("Bearer ")) {
                                        return;
                                    }
                                    jwt = header.substring(7);
                                    if (!jwt.isEmpty()) {
                                        SecurityContextHolder.clearContext();
                                    }
                                })
                                .logoutSuccessHandler((request, response, auth) -> SecurityContextHolder.clearContext())
                )
                .getOrBuild();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(List.of(AUTHORIZATION));
        for (String corsProperty : corsProperties.getAllowedOrigins()) {
            configuration.addAllowedOrigin(corsProperty);
        }
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList(AUTHORIZATION, "Requestor-Type", "Content-Type", "Access-Control-Allow-Headers", "Access-Control-Allow-Origin"));
        configuration.setExposedHeaders(Arrays.asList("X-Get-Header", "Access-Control-Allow-Methods", "Access-Control-Allow-Origin"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
