package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createRounds;
import static com.mpusinhol.rockpaperscissors.model.dto.UserMovesStubs.createAnonymousUserMoves;

public class GameDTOStubs {

    public static GameDTO createOngoingGame() {
        return new GameDTO("1", new ArrayList<>(), createAnonymousUserMoves(), Instant.now(), null, null);
    }

    public static GameDTO createFinishedGame() {
        return new GameDTO("2", createRounds(), createAnonymousUserMoves(), Instant.now(), Instant.now().plus(1, ChronoUnit.MINUTES), Winner.PLAYER);
    }
}
