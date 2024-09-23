package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;

import java.util.List;

public record UserMoves(String username, List<Move> moves, Move mostPlayedMove) {
}
