package com.kkimleang.rrms.config.oauth2.service;

import com.kkimleang.rrms.config.oauth2.user.OAuth2UserInfo;
import com.kkimleang.rrms.config.oauth2.user.OAuth2UserInfoFactory;
import com.kkimleang.rrms.entity.Role;
import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.enums.user.AuthProvider;
import com.kkimleang.rrms.enums.user.AuthRole;
import com.kkimleang.rrms.exception.OAuth2AuthenticationProcessingException;
import com.kkimleang.rrms.repository.user.UserRepository;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import com.kkimleang.rrms.service.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the
            // OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider()
                    .equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))
            ) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return new CustomUserDetails(user, oAuth2UserInfo.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProvider(AuthProvider.valueOf(oAuth2UserInfo.getId().toUpperCase()));
        user.setUsername(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        Role role = roleService.findByName(AuthRole.USER.name());
        user.setRoles(Set.of(role));
        user.setProfilePicture(oAuth2UserInfo.getProfilePicture());
        user = userRepository.save(user);
        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setUsername(oAuth2UserInfo.getName());
        existingUser.setProfilePicture(oAuth2UserInfo.getProfilePicture());
        return userRepository.save(existingUser);
    }

}
