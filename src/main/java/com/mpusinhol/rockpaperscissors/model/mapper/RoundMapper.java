package com.mpusinhol.rockpaperscissors.model.mapper;

import com.mpusinhol.rockpaperscissors.model.dto.RoundDTO;
import com.mpusinhol.rockpaperscissors.model.entity.Round;

public class RoundMapper {

    public static Round toEntity(RoundDTO roundDTO) {
        return Round.builder()
                .roundNumber(roundDTO.roundNumber())
                .computerMove(roundDTO.computerMove())
                .playerMove(roundDTO.playerMove())
                .winner(roundDTO.winner())
                .build();
    }

    public static RoundDTO toDTO(Round round) {
        return new RoundDTO(
                round.getRoundNumber(),
                round.getComputerMove(),
                round.getPlayerMove(),
                round.getWinner());
    }
}
