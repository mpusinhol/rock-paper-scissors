package com.mpusinhol.rockpaperscissors.utils;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundDTO;
import com.mpusinhol.rockpaperscissors.model.dto.RoundResponse;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mpusinhol.rockpaperscissors.model.dto.GameDTOStubs.createFinishedGame;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createDifferentWinnersRoundsDifferentMove;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createDifferentWinnersRoundsSameMove;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createPlayerLosingRoundsDifferentMove;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createPlayerLosingRoundsSameMove;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createPlayerWinningRoundsDifferentMove;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createPlayerWinningRoundsSameMove;
import static com.mpusinhol.rockpaperscissors.model.dto.UserMovesStubs.createAuthenticatedUserMoves;
import static com.mpusinhol.rockpaperscissors.utils.GameUtils.generateNextMove;
import static com.mpusinhol.rockpaperscissors.utils.GameUtils.generateStatistics;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameUtilsTest {

    @Nested
    class GenerateNextMoveTests {

        @Test
        void givenAuthenticatedPlayer_firstRound_shouldCounterMostPlayedMove() {
            assertEquals(Move.SCISSORS, generateNextMove(createAuthenticatedUserMoves(), List.of()));
        }

        @Test
        void givenLastTwoRounds_sameMoves_playerWinning_shouldCounterSameMove() {
            List<RoundDTO> rounds = createPlayerWinningRoundsSameMove();
            assertEquals(Move.SCISSORS, generateNextMove(createAuthenticatedUserMoves(), rounds));
        }

        @Test
        void givenLastTwoRounds_sameMoves_playerLosing_shouldCounterOtherMoves() {
            List<RoundDTO> rounds = createPlayerLosingRoundsSameMove();
            assertNotEquals(Move.SCISSORS, generateNextMove(createAuthenticatedUserMoves(), rounds));
        }

        @Test
        void givenLastTwoRounds_sameMoves_differentWinners_shouldGoRandom() {
            List<RoundDTO> rounds = createDifferentWinnersRoundsSameMove();
            assertNotNull(generateNextMove(createAuthenticatedUserMoves(), rounds));
        }

        @Test
        void givenLastTwoRounds_differentMoves_playerWinning_shouldCounterRemainingMove() {
            List<RoundDTO> rounds = createPlayerWinningRoundsDifferentMove();
            assertEquals(Move.PAPER, generateNextMove(createAuthenticatedUserMoves(), rounds));
        }

        @Test
        void givenLastTwoRounds_differentMoves_PlayerWonThenLost_shouldCounterPenultimateMove() {
            List<RoundDTO> rounds = createDifferentWinnersRoundsDifferentMove();
            assertNotNull(generateNextMove(createAuthenticatedUserMoves(), rounds));
        }

        @Test
        void givenLastTwoRounds_differentMoves_playerLosing_shouldGoRandom() {
            List<RoundDTO> rounds = createPlayerLosingRoundsDifferentMove();
            assertNotNull(generateNextMove(createAuthenticatedUserMoves(), rounds));
        }

        @Test
        void givenAnonymousPlayer_firstRound_shouldGoRandom() {
            assertNotNull(generateNextMove(null, List.of()));
        }
    }

    @Nested
    class DetermineRoundTests {

        @Test
        void shouldDetermineTie() {
            RoundResponse roundResponse = GameUtils.determineRoundWinner(1, Move.ROCK, Move.ROCK);
            assertNotNull(roundResponse);
            assertEquals(Winner.TIE, roundResponse.roundDTO().winner());
            assertEquals(roundResponse.message(), "It's a tie!");
        }

        @Test
        void shouldDetermineComputerWinner() {
            RoundResponse roundResponse = GameUtils.determineRoundWinner(1, Move.PAPER, Move.ROCK);
            assertNotNull(roundResponse);
            assertEquals(Winner.COMPUTER, roundResponse.roundDTO().winner());
            assertEquals(roundResponse.message(), "I won this round!");
        }

        @Test
        void shouldDeterminePlayerWinner() {
            RoundResponse roundResponse = GameUtils.determineRoundWinner(1, Move.PAPER, Move.SCISSORS);
            assertNotNull(roundResponse);
            assertEquals(Winner.PLAYER, roundResponse.roundDTO().winner());
            assertEquals(roundResponse.message(), "You won this round!");
        }
    }

    @Nested
    class GenerateStatisticsTests {

        @Test
        void shouldGenerateStatisticsCorrectly() {
            GameDTO gameDTO = createFinishedGame();
            GameStatistics gameStatistics = generateStatistics(gameDTO);
            assertEquals(gameDTO.id(), gameStatistics.gameId());
            assertEquals(gameDTO.rounds().size(), gameStatistics.numberOfRounds());
            assertEquals(0, gameStatistics.roundsWonByComputer());
            assertEquals(1, gameStatistics.roundsWonByPlayer());
            assertEquals(1, gameStatistics.roundsTied());
            assertEquals(Move.PAPER, gameStatistics.playerMostPlayedMove());
            assertEquals(Winner.PLAYER, gameStatistics.gameWinner());
            assertEquals(gameDTO.rounds(), gameStatistics.rounds());
        }
    }
}
