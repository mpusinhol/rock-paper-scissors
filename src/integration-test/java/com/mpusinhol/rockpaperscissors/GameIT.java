package com.mpusinhol.rockpaperscissors;

import com.mpusinhol.rockpaperscissors.model.dto.ExceptionDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.entity.Game;
import com.mpusinhol.rockpaperscissors.model.entity.User;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.enumeration.Winner;
import com.mpusinhol.rockpaperscissors.service.GameService;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;
import static com.mpusinhol.rockpaperscissors.model.entity.UserStubs.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameIT extends BaseIT {

    @Autowired
    private GameService gameService;
    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createUser());
    }

    @AfterEach
    void cleanUp() {
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    class StartGameTests {

        @Test
        void givenGuestUser_shouldStartGameSuccessfully() throws Exception {
            String location = mockMvc.perform(
                            MockMvcRequestBuilders.post("/games/start")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.header().exists("Location"))
                    .andReturn().getResponse().getHeader("Location");

            assertNotNull(location);

            String gameId = location.substring(location.lastIndexOf("/") + 1);
            Optional<GameDTO> gameDTO = gameCacheService.getGame(gameId);

            assertTrue(gameDTO.isPresent());
            assertNotNull(gameDTO.get().userMoves());
            assertEquals(ANONYMOUS_USER, gameDTO.get().userMoves().username());
            assertNull(gameDTO.get().winner());
            assertNotNull(gameDTO.get().start());
            assertNull(gameDTO.get().end());

            assertTrue(gameRepository.findById(gameId).isEmpty());
        }

        @Test
        void givenAuthenticatedUser_shouldStartGameSuccessfully() throws Exception {
            String token = generateAuthenticationToken(user.getUsername());

            String location = mockMvc.perform(
                            MockMvcRequestBuilders.post("/games/start")
                                    .header("Authorization", token)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.header().exists("Location"))
                    .andReturn().getResponse().getHeader("Location");

            assertNotNull(location);

            String gameId = location.substring(location.lastIndexOf("/") + 1);
            Optional<GameDTO> gameDTO = gameCacheService.getGame(gameId);

            assertTrue(gameDTO.isPresent());
            assertNotNull(gameDTO.get().userMoves());
            assertEquals(user.getUsername(), gameDTO.get().userMoves().username());
            assertNull(gameDTO.get().winner());
            assertNotNull(gameDTO.get().start());
            assertNull(gameDTO.get().end());

            assertTrue(gameRepository.findById(gameId).isEmpty());
        }
    }

    @Nested
    class MakeMoveTests {
        private String gameId;

        @BeforeEach
        void setUp() {
            gameId = gameService.startGame(null).id();
        }

        @Test
        void shouldMakeMoveSuccessfully() throws Exception {
            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/games/%s/move".formatted(gameId))
                                    .content("\"ROCK\"")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertNotNull(response);

            Optional<GameDTO> gameDTO = gameCacheService.getGame(gameId);

            assertTrue(gameDTO.isPresent());
            assertNotNull(gameDTO.get().userMoves());
            assertEquals(ANONYMOUS_USER, gameDTO.get().userMoves().username());
            assertNull(gameDTO.get().winner());
            assertNotNull(gameDTO.get().start());
            assertNull(gameDTO.get().end());
            assertFalse(gameDTO.get().rounds().isEmpty());
            assertEquals(Move.ROCK, gameDTO.get().rounds().getFirst().playerMove());
            assertNotNull(gameDTO.get().rounds().getFirst().winner());

            assertTrue(gameRepository.findById(gameId).isEmpty());
        }

        @Test
        void shouldReturnNotFound_whenGameIdDoesNotExist() throws Exception {
            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/games/1/move")
                                    .content("\"ROCK\"")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertNotNull(response);
            ExceptionDTO exceptionDTO = objectMapper.readValue(response, ExceptionDTO.class);
            assertNotNull(exceptionDTO.time());
            assertEquals(404, exceptionDTO.code());
            assertEquals("Game 1 does not exist or is not ongoing", exceptionDTO.message());
        }
    }

    @Nested
    class TerminateTests {
        private String gameId;

        @BeforeEach
        void setUp() {
            gameId = gameService.startGame(null).id();
        }

        @AfterEach
        void cleanUp() {
            gameCacheService.deleteGame(gameId);
        }

        @Test
        void GivenAFewRounds_shouldTerminateSuccessfully() throws Exception {
            makeRounds(gameId, 5);

            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/games/%s/terminate".formatted(gameId))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertNotNull(response);
            GameStatistics gameStatistics = objectMapper.readValue(response, GameStatistics.class);

            assertNotNull(gameStatistics);
            assertEquals(5, gameStatistics.numberOfRounds());
            assertEquals(5, gameStatistics.rounds().size());
            assertNotNull(gameStatistics.gameWinner());
            assertNotNull(gameStatistics.gameId());
            assertNotNull(gameStatistics.roundsWonByComputer());
            assertNotNull(gameStatistics.roundsWonByPlayer());
            assertNotNull(gameStatistics.roundsTied());
            assertNotNull(gameStatistics.playerMostPlayedMove());

            Optional<GameDTO> gameDTO = gameCacheService.getGame(gameId);
            assertTrue(gameDTO.isEmpty());

            Optional<Game> game = gameRepository.findById(gameId);
            assertTrue(game.isPresent());
            assertEquals(5, game.get().getRounds().size());
            assertNotNull(game.get().getWinner());
            assertNotNull(game.get().getStartTime());
            assertNotNull(game.get().getEndTime());
            assertNotNull(game.get().getPlayerMostPlayedMove());
        }

        @Test
        void GivenNoRounds_shouldTerminateSuccessfully() throws Exception {
            String response = mockMvc.perform(
                            MockMvcRequestBuilders.post("/games/%s/terminate".formatted(gameId))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertNotNull(response);
            GameStatistics gameStatistics = objectMapper.readValue(response, GameStatistics.class);

            assertNotNull(gameStatistics);
            assertEquals(0, gameStatistics.numberOfRounds());
            assertEquals(0, gameStatistics.rounds().size());
            assertEquals(Winner.TIE, gameStatistics.gameWinner());
            assertNotNull(gameStatistics.gameId());
            assertEquals(0, gameStatistics.roundsWonByComputer());
            assertEquals(0, gameStatistics.roundsWonByPlayer());
            assertEquals(0, gameStatistics.roundsTied());
            assertNull(gameStatistics.playerMostPlayedMove());

            Optional<GameDTO> gameDTO = gameCacheService.getGame(gameId);
            assertTrue(gameDTO.isEmpty());

            Optional<Game> game = gameRepository.findById(gameId);
            assertTrue(game.isPresent());
            assertEquals(0, game.get().getRounds().size());
            assertEquals(Winner.TIE, game.get().getWinner());
            assertNotNull(game.get().getStartTime());
            assertNotNull(game.get().getEndTime());
            assertNull(game.get().getPlayerMostPlayedMove());
        }
    }

    @Nested
    class FullGameTests {

        @Test
        void shouldRunSeveralGamesInParallelSuccessfully() throws ExecutionException, InterruptedException {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                futures.add(executor.submit(() -> {
                    String gameId = gameService.startGame("").id();
                    int rounds = new Random().nextInt(1, 6);
                    try {
                        makeRounds(gameId, rounds);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    GameStatistics gameStatistics = gameService.terminateGame(gameId);
                    assertNotNull(gameStatistics);
                    assertEquals(rounds, gameStatistics.numberOfRounds());
                    assertEquals(rounds, gameStatistics.rounds().size());
                    assertNotNull(gameStatistics.gameWinner());
                    assertNotNull(gameStatistics.gameId());
                    assertNotNull(gameStatistics.roundsWonByComputer());
                    assertNotNull(gameStatistics.roundsWonByPlayer());
                    assertNotNull(gameStatistics.roundsTied());
                    assertNotNull(gameStatistics.playerMostPlayedMove());

                    return null;
                }));
            }

            for(Future<Void> future : futures) {
                future.get();
            }

            List<Game> games = gameRepository.findAll();
            assertNotNull(games);
            assertEquals(5, games.size());

            executor.shutdown();
        }
    }

    private String makeGame() {
        return gameService.startGame("").id();
    }

    private void makeRounds(String gameId, int numberOfRounds) throws Exception {
        Random random = new Random();
        for(int i = 0; i < numberOfRounds; i++) {
            Move move = Move.values()[random.nextInt(3)];
            gameService.makeMove(gameId, move);
        }
    }
}
