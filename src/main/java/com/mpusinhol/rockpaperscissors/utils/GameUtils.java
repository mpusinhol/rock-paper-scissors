package com.mpusinhol.rockpaperscissors.utils;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundDTO;
import com.mpusinhol.rockpaperscissors.model.dto.RoundResponse;
import com.mpusinhol.rockpaperscissors.model.dto.UserMoves;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class GameUtils {

    public static Move generateNextMove(UserMoves userMoves, List<RoundDTO> lastRounds) {
        //If it's a new game, we counter the most played move
        if (lastRounds.isEmpty() && !isNull(userMoves) && !isNull(userMoves.mostPlayedMove())) {
            return getCounterPlay(userMoves.mostPlayedMove());
        }

        if (lastRounds.size() >= 2) {
            Move lastMove = lastRounds.getLast().playerMove();
            Winner lastWinner = lastRounds.getLast().winner();
            Move penultimateMove = lastRounds.get(lastRounds.size() - 2).playerMove();
            Winner penultimateWinner = lastRounds.get(lastRounds.size() - 2).winner();

            if (lastMove == penultimateMove) {
                return handleSameMove(lastMove, lastWinner, penultimateWinner);
            }

            return handleDifferentMoves(lastMove, penultimateMove, lastWinner, penultimateWinner);
        }

        //Last case we go random
        return generateRandomMove(Arrays.asList(Move.values()));
    }

    public static RoundResponse determineRoundWinner(Integer round, Move computerMove, Move playerMove) {
        return switch (computerMove.compareMove(playerMove)) {
            case 0 -> new RoundResponse(new RoundDTO(round, computerMove, playerMove, Winner.TIE), "It's a tie!");
            case 1 -> new RoundResponse(new RoundDTO(round, computerMove, playerMove, Winner.COMPUTER), "I won this round!");
            default -> new RoundResponse(new RoundDTO(round, computerMove, playerMove, Winner.PLAYER), "You won this round!");
        };
    }

    public static GameStatistics generateStatistics(GameDTO gameDTO) {
        //If performance ends up as a problem below, we can refactor the streams to use a single loop
        Map<Winner, List<RoundDTO>> winnersMap = gameDTO.rounds()
                .stream()
                .collect(groupingBy(RoundDTO::winner));
        Move mostPlayedMove = gameDTO.rounds()
                .stream()
                .map(RoundDTO::playerMove)
                .collect(groupingBy(Function.identity(), counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Winner winner = Winner.TIE;
        int playerWins = winnersMap.getOrDefault(Winner.PLAYER, List.of()).size();
        int computerWins = winnersMap.getOrDefault(Winner.COMPUTER, List.of()).size();
        if (playerWins > computerWins) {
            winner = Winner.PLAYER;
        } else if (computerWins > playerWins) {
            winner = Winner.COMPUTER;
        }

        return new GameStatistics(
                gameDTO.id(),
                gameDTO.rounds().size(),
                computerWins,
                playerWins,
                winnersMap.getOrDefault(Winner.TIE, List.of()).size(),
                mostPlayedMove,
                winner,
                gameDTO.rounds()
        );
    }

    private static Move getCounterPlay(Move move) {
        return switch (move) {
            case ROCK -> Move.PAPER;
            case PAPER -> Move.SCISSORS;
            case SCISSORS -> Move.ROCK;
        };
    }

    private static Move handleSameMove(Move move, Winner lastWinner, Winner penultimateWinner) {
        if (lastWinner == Winner.PLAYER && penultimateWinner == Winner.PLAYER) {
            //If he is winning with the same move, he might repeat it again
            return getCounterPlay(move);
        }
        if (lastWinner == Winner.COMPUTER && penultimateWinner == Winner.COMPUTER) {
            //In case he is losing, he is likely to change
            return getCounterPlay(generateRandomMove(findRemainingMoves(move)));
        }
        return generateRandomMove(Arrays.asList(Move.values()));
    }

    private static Move handleDifferentMoves(Move lastMove, Move penultimateMove, Winner lastWinner, Winner penultimateWinner) {
        if (lastWinner == Winner.PLAYER && penultimateWinner == Winner.PLAYER) {
            //If he won with different moves, he might try the third one
            return getCounterPlay(findRemainingMove(Set.of(lastMove, penultimateMove)));
        }
        if (lastWinner == Winner.COMPUTER && penultimateWinner == Winner.PLAYER) {
            //If he won and then lost, he might go back the one he won
            return getCounterPlay(penultimateMove);
        }
        return generateRandomMove(Arrays.asList(Move.values()));
    }

    private static Move generateRandomMove(List<Move> moves) {
        return moves.get(new Random().nextInt(moves.size()));
    }

    private static List<Move> findRemainingMoves(Move lastMove) {
        return switch (lastMove) {
            case ROCK -> List.of(Move.PAPER, Move.SCISSORS);
            case PAPER -> List.of(Move.ROCK, Move.SCISSORS);
            case SCISSORS -> List.of(Move.PAPER, Move.ROCK);
        };
    }

    private static Move findRemainingMove(Set<Move> moves) {
        if (!moves.contains(Move.ROCK)) {
            return Move.ROCK;
        }
        if (!moves.contains(Move.PAPER)) {
            return Move.PAPER;
        }
        return Move.SCISSORS;
    }
}
