package com.mpusinhol.rockpaperscissors.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AuthenticationResponse(String token, String tokenType, Instant expiresAt, UserDTO user) {}
