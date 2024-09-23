package com.mpusinhol.rockpaperscissors.service;

import com.mpusinhol.rockpaperscissors.model.dto.AuthenticationResponse;
import com.mpusinhol.rockpaperscissors.model.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);
    AuthenticationResponse authenticate(UsernamePasswordAuthenticationToken token);
}
