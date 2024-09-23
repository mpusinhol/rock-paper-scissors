package com.mpusinhol.rockpaperscissors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpusinhol.rockpaperscissors.configuration.PostgresContainerConfiguration;
import com.mpusinhol.rockpaperscissors.configuration.RedisContainerConfiguration;
import com.mpusinhol.rockpaperscissors.repository.GameRepository;
import com.mpusinhol.rockpaperscissors.repository.UserRepository;
import com.mpusinhol.rockpaperscissors.service.GameCacheService;
import com.mpusinhol.rockpaperscissors.service.JWTService;
import com.mpusinhol.rockpaperscissors.service.impl.JWTServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = {PostgresContainerConfiguration.class, RedisContainerConfiguration.class})
@AutoConfigureMockMvc
public class BaseIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected GameCacheService gameCacheService;

    @Autowired
    protected JWTService jwtService;

    @Autowired
    protected GameRepository gameRepository;

    protected String generateAuthenticationToken(String username) {
        String token = jwtService.generateToken(username);

        return JWTServiceImpl.TOKEN_TYPE.concat(" ").concat(token);
    }
}
