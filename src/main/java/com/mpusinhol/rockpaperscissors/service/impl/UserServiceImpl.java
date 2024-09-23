package com.mpusinhol.rockpaperscissors.service.impl;

import com.mpusinhol.rockpaperscissors.exception.ObjectDuplicateException;
import com.mpusinhol.rockpaperscissors.model.dto.AuthenticationResponse;
import com.mpusinhol.rockpaperscissors.model.dto.UserDTO;
import com.mpusinhol.rockpaperscissors.model.entity.User;
import com.mpusinhol.rockpaperscissors.model.mapper.UserMapper;
import com.mpusinhol.rockpaperscissors.repository.UserRepository;
import com.mpusinhol.rockpaperscissors.service.UserService;
import com.mpusinhol.rockpaperscissors.service.impl.JWTServiceImpl;
import com.mpusinhol.rockpaperscissors.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JWTServiceImpl jwtService;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (ANONYMOUS_USER.equals(userDTO.username())) {
            throw new ObjectDuplicateException("Username %s already exists".formatted(userDTO.username()));
        }

        userRepository.findByUsername(userDTO.username())
                .ifPresent(user -> {
                    throw new ObjectDuplicateException("Username %s already exists".formatted(userDTO.username()));
                });

        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    public AuthenticationResponse authenticate(UsernamePasswordAuthenticationToken token) {
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(token.getPrincipal().toString());

        String jwt = jwtService.generateToken(userDetails.getUsername());
        Instant expiration = jwtService.getExpiration(jwt);

        UserDTO userDTO =
                userRepository
                        .findByUsername(userDetails.getUsername())
                        .map(UserMapper::toDTO)
                        .orElse(null);

        return new AuthenticationResponse(jwt, JWTServiceImpl.TOKEN_TYPE, expiration, userDTO);
    }
}
