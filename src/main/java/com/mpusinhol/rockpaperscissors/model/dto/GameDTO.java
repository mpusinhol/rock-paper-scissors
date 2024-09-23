package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;

import java.time.Instant;
import java.util.List;

public record GameDTO(
        String id,
        List<RoundDTO> rounds,
        UserMoves userMoves,
        Instant start,
        Instant end,
        Winner winner) {
}
