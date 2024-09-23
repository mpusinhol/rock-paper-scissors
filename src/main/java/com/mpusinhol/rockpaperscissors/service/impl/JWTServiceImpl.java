package com.mpusinhol.rockpaperscissors.service.impl;

import com.mpusinhol.rockpaperscissors.configuration.properties.JWTProperties;
import com.mpusinhol.rockpaperscissors.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {
    public static final String TOKEN_TYPE = "Bearer";

    private final JWTProperties jwtProperties;

    @Override
    public String generateToken(String username) {
        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + jwtProperties.getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Instant getExpiration(String token) {
        return getTokenPayload(token).getExpiration().toInstant();
    }

    @Override
    public boolean isTokenValid(String token) {
        if (isNull(token) || !token.startsWith(TOKEN_TYPE)) {
            return false;
        }

        Claims claims = getTokenPayload(getJWT(token));
        return !isNull(claims) && claims.getExpiration().before(new Date());
    }

    @Override
    public String getUsername(String token) {
        return getTokenPayload(getJWT(token)).getSubject();
    }

    private Claims getTokenPayload(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private String getJWT(String token) {
        return token.substring(TOKEN_TYPE.length() + 1);
    }
}
