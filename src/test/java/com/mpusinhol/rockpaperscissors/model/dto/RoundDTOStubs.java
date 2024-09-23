package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;

import java.util.List;

public class RoundDTOStubs {

    public static List<RoundDTO> createRounds() {
        return List.of(
                new RoundDTO(1, Move.ROCK, Move.PAPER, Winner.PLAYER),
                new RoundDTO(2, Move.PAPER, Move.PAPER, Winner.TIE));
    }

    public static List<RoundDTO> createPlayerWinningRoundsSameMove() {
        return List.of(
                new RoundDTO(1, Move.ROCK, Move.PAPER, Winner.PLAYER),
                new RoundDTO(2, Move.ROCK, Move.PAPER, Winner.PLAYER));
    }

    public static List<RoundDTO> createPlayerLosingRoundsSameMove() {
        return List.of(
                new RoundDTO(1, Move.SCISSORS, Move.PAPER, Winner.COMPUTER),
                new RoundDTO(2, Move.SCISSORS, Move.PAPER, Winner.COMPUTER));
    }

    public static List<RoundDTO> createDifferentWinnersRoundsSameMove() {
        return List.of(
                new RoundDTO(1, Move.SCISSORS, Move.PAPER, Winner.COMPUTER),
                new RoundDTO(2, Move.ROCK, Move.PAPER, Winner.PLAYER));
    }

    public static List<RoundDTO> createPlayerWinningRoundsDifferentMove() {
        return List.of(
                new RoundDTO(1, Move.ROCK, Move.PAPER, Winner.PLAYER),
                new RoundDTO(2, Move.PAPER, Move.SCISSORS, Winner.PLAYER));
    }

    public static List<RoundDTO> createPlayerLosingRoundsDifferentMove() {
        return List.of(
                new RoundDTO(1, Move.SCISSORS, Move.PAPER, Winner.COMPUTER),
                new RoundDTO(2, Move.PAPER, Move.ROCK, Winner.COMPUTER));
    }

    public static List<RoundDTO> createDifferentWinnersRoundsDifferentMove() {
        return List.of(
                new RoundDTO(1, Move.ROCK, Move.PAPER, Winner.PLAYER),
                new RoundDTO(2, Move.PAPER, Move.ROCK, Winner.COMPUTER));
    }
}
