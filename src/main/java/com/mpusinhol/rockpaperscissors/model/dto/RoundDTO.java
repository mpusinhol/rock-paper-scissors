package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;

public record RoundDTO(Integer roundNumber, Move computerMove, Move playerMove, Winner winner) {
}
