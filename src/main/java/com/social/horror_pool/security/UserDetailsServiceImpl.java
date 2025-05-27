package com.social.horror_pool.security;

import com.social.horror_pool.model.User;
import com.social.horror_pool.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new CustomUserDetails(user);
    }
}
