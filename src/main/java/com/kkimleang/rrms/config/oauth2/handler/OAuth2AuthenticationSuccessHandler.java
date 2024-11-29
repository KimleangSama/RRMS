package com.kkimleang.rrms.config.oauth2.handler;

import com.kkimleang.rrms.config.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.kkimleang.rrms.config.properties.OAuthProperties;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.exception.BadRequestException;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.repository.user.UserRepository;
import com.kkimleang.rrms.util.CookieUtils;
import com.kkimleang.rrms.util.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

import static com.kkimleang.rrms.config.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final OAuthProperties oauthProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException(
                    "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", "email");
        }
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return Stream.of(oauthProperties.getAuthorizedRedirectUris())
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
