package com.mpusinhol.rockpaperscissors.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application.jwt")
@Data
public class JWTProperties {
    private String secret;
    private long expiration;
}
