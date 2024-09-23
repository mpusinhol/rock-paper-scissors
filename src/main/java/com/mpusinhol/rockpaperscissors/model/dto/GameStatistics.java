package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;

import java.util.List;

public record GameStatistics(
        String gameId,
        Integer numberOfRounds,
        Integer roundsWonByComputer,
        Integer roundsWonByPlayer,
        Integer roundsTied,
        Move playerMostPlayedMove,
        Winner gameWinner,
        List<RoundDTO> rounds) {
}
