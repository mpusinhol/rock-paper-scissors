package com.mpusinhol.rockpaperscissors.controller;

import com.mpusinhol.rockpaperscissors.model.dto.AuthenticationRequest;
import com.mpusinhol.rockpaperscissors.model.dto.AuthenticationResponse;
import com.mpusinhol.rockpaperscissors.model.dto.UserDTO;
import com.mpusinhol.rockpaperscissors.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping({"/users"})
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO user) {
        UserDTO newUser = userServiceImpl.createUser(user);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.id())
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                authenticationRequest.username(), authenticationRequest.password());

        return ResponseEntity.ok(userServiceImpl.authenticate(token));
    }
}
