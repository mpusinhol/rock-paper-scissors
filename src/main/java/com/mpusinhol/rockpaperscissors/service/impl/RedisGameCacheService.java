package com.mpusinhol.rockpaperscissors.service.impl;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.service.GameCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGameCacheService implements GameCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveGame(GameDTO game) {
        redisTemplate.opsForValue().set(game.id(), game);
    }

    @Override
    public Optional<GameDTO> getGame(String gameId) {
        Object game = redisTemplate.opsForValue().get(gameId);
        log.debug("GameId: {} | game: {}", gameId, game);

        return Optional.ofNullable((GameDTO) game);
    }

    @Override
    public void deleteGame(String gameId) {
        redisTemplate.delete(gameId);
    }
}
