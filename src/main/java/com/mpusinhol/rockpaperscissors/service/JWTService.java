package com.mpusinhol.rockpaperscissors.service;

import java.time.Instant;

public interface JWTService {
    String generateToken(String username);
    Instant getExpiration(String token);
    String getUsername(String token);
    boolean isTokenValid(String token);
}
