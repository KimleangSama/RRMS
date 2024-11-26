package com.kkimleang.rrms.service.user;

import com.kkimleang.rrms.entity.User;
import com.kkimleang.rrms.repository.user.*;
import java.util.*;
import lombok.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

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
