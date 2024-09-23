package com.mpusinhol.rockpaperscissors.service;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundResponse;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;

public interface GameService {
    GameDTO startGame(String username);
    RoundResponse makeMove(String gameId, Move playerMove);
    GameStatistics terminateGame(String gameId);
}
