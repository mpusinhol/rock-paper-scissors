package com.mpusinhol.rockpaperscissors.model.mapper;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundDTO;
import com.mpusinhol.rockpaperscissors.model.dto.UserMoves;
import com.mpusinhol.rockpaperscissors.model.entity.Game;
import com.mpusinhol.rockpaperscissors.model.entity.Round;

import java.util.List;

import static java.util.Objects.isNull;

public class GameMapper {

    public static Game toEntity(GameDTO gameDTO, GameStatistics gameStatistics) {
        List<Round> rounds = gameDTO.rounds()
                .stream()
                .map(RoundMapper::toEntity)
                .toList();

        Game.GameBuilder game = Game.builder()
                .id(gameDTO.id())
                .startTime(gameDTO.start())
                .endTime(gameDTO.end())
                .winner(gameDTO.winner())
                .rounds(rounds);

        if (!isNull(gameStatistics)) {
            game
                    .winner(gameStatistics.gameWinner()) //Overriding winner in case we have it
                    .roundsWonByComputer(gameStatistics.roundsWonByComputer())
                    .roundsWonByPlayer(gameStatistics.roundsWonByPlayer())
                    .roundsTied(gameStatistics.roundsTied())
                    .playerMostPlayedMove(gameStatistics.playerMostPlayedMove());
        }

        return game.build();
    }

    public static GameDTO toDTO(Game game, UserMoves userMoves) {
        List<RoundDTO> rounds = game.getRounds()
                .stream()
                .map(RoundMapper::toDTO)
                .toList();

        return new GameDTO(
                game.getId(),
                rounds,
                userMoves,
                game.getStartTime(),
                game.getEndTime(),
                game.getWinner());
    }
}
