package com.kkimleang.rrms.service.user;

import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    //    @Cacheable(value = "CustomUserDetails", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username)));
        User u = user.orElse(null);
        return new CustomUserDetails(u, null);
    }
}
