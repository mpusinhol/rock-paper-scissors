package com.mpusinhol.rockpaperscissors.service;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;

import java.util.Optional;

public interface GameCacheService {

    void saveGame(GameDTO game);

    Optional<GameDTO> getGame(String gameId);

    void deleteGame(String gameId);
}
