package com.mpusinhol.rockpaperscissors.service.impl;

import com.mpusinhol.rockpaperscissors.model.entity.User;
import com.mpusinhol.rockpaperscissors.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword())
                .build();
    }
}
