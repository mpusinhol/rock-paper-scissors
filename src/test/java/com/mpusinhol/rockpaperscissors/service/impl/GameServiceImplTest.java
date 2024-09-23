package com.mpusinhol.rockpaperscissors.service.impl;

import com.mpusinhol.rockpaperscissors.exception.ObjectNotFoundException;
import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundResponse;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;
import com.mpusinhol.rockpaperscissors.repository.GameRepository;
import com.mpusinhol.rockpaperscissors.repository.UserRepository;
import com.mpusinhol.rockpaperscissors.service.GameCacheService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;
import static com.mpusinhol.rockpaperscissors.model.dto.GameDTOStubs.createOngoingGame;
import static com.mpusinhol.rockpaperscissors.model.dto.RoundDTOStubs.createRounds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameCacheService gameCacheService;

    @Nested
    class StartGameTests {

        @Test
        void shouldStartAnonymousGameSuccessfully() {
            doNothing().when(gameCacheService).saveGame(any(GameDTO.class));

            GameDTO gameDTO = gameService.startGame("");
            assertNotNull(gameDTO);
            assertNotNull(gameDTO.id());
            assertTrue(gameDTO.rounds().isEmpty());
            assertNotNull(gameDTO.userMoves());
            assertEquals(ANONYMOUS_USER, gameDTO.userMoves().username());
            assertTrue(gameDTO.start().isBefore(Instant.now()));
            assertNull(gameDTO.end());
            assertNull(gameDTO.winner());

            verify(gameCacheService, times(1)).saveGame(gameDTO);
            verifyNoInteractions(gameRepository);
        }

        @Test
        void shouldStartAuthenticatedUserGameSuccessfully() {
            List<Move> moves = List.of(Move.ROCK, Move.PAPER, Move.ROCK);
            when(gameRepository.findLastPlayerMovesByUsername("mpusinhol", PageRequest.of(0, 50)))
                    .thenReturn(moves);
            doNothing().when(gameCacheService).saveGame(any(GameDTO.class));

            GameDTO gameDTO = gameService.startGame("mpusinhol");
            assertNotNull(gameDTO);
            assertNotNull(gameDTO.id());
            assertTrue(gameDTO.rounds().isEmpty());
            assertNotNull(gameDTO.userMoves());
            assertEquals("mpusinhol", gameDTO.userMoves().username());
            assertEquals(moves, gameDTO.userMoves().moves());
            assertEquals(Move.ROCK, gameDTO.userMoves().mostPlayedMove());
            assertTrue(gameDTO.start().isBefore(Instant.now()));
            assertNull(gameDTO.end());
            assertNull(gameDTO.winner());

            verify(gameCacheService, times(1)).saveGame(gameDTO);
            verifyNoMoreInteractions(gameRepository);
        }
    }

    @Nested
    class MakeMoveTests {

        @Test
        void shouldMakeMoveSuccessfully() {
            GameDTO gameDTO = createOngoingGame();
            when(gameCacheService.getGame(gameDTO.id())).thenReturn(Optional.of(createOngoingGame()));
            doNothing().when(gameCacheService).saveGame(any(GameDTO.class));

            RoundResponse roundResponse = gameService.makeMove(gameDTO.id(), Move.PAPER);
            assertNotNull(roundResponse);
            assertNotNull(roundResponse.message());
            assertNotNull(roundResponse.roundDTO());
            assertEquals(1, roundResponse.roundDTO().roundNumber());
            assertEquals(Move.PAPER, roundResponse.roundDTO().playerMove());
            assertNotNull(roundResponse.roundDTO().computerMove());
            assertNotNull(roundResponse.roundDTO().winner());

            verify(gameCacheService, times(1)).saveGame(any());
            verifyNoInteractions(gameRepository);
        }

        @Test
        void shouldThrowExceptionIfGameIdIsNotFound() {
            when(gameCacheService.getGame("1")).thenReturn(Optional.empty());
            Exception e = assertThrows(ObjectNotFoundException.class, () -> gameService.makeMove("1", Move.PAPER));
            assertEquals("Game 1 does not exist or is not ongoing", e.getMessage());
        }
    }

    @Nested
    class TerminateGameTests {

        @Test
        void shouldTerminateGameSuccessfully() {
            GameDTO gameDTO = createOngoingGame();
            gameDTO.rounds().addAll(createRounds());

            when(gameCacheService.getGame(gameDTO.id())).thenReturn(Optional.of(gameDTO));
            doNothing().when(gameCacheService).deleteGame(gameDTO.id());
            when(gameRepository.save(any())).thenReturn(null);

            GameStatistics gameStatistics = gameService.terminateGame(gameDTO.id());
            assertNotNull(gameStatistics);
            assertEquals(Move.PAPER, gameStatistics.playerMostPlayedMove());
            assertEquals(Winner.PLAYER, gameStatistics.gameWinner());
            assertEquals(2, gameStatistics.rounds().size());
            assertEquals(1, gameStatistics.roundsTied());
            assertEquals(1, gameStatistics.roundsWonByPlayer());
            assertEquals(0, gameStatistics.roundsWonByComputer());

            verify(gameCacheService, times(1)).deleteGame(any());
        }

        @Test
        void shouldThrowExceptionIfGameIdIsNotFound() {
            when(gameCacheService.getGame("1")).thenReturn(Optional.empty());
            Exception e = assertThrows(ObjectNotFoundException.class, () -> gameService.makeMove("1", Move.PAPER));
            assertEquals("Game 1 does not exist or is not ongoing", e.getMessage());
        }
    }
}
