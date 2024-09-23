package com.mpusinhol.rockpaperscissors.service.impl;

import com.mpusinhol.rockpaperscissors.exception.ObjectNotFoundException;
import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundResponse;
import com.mpusinhol.rockpaperscissors.model.dto.UserMoves;
import com.mpusinhol.rockpaperscissors.model.entity.Game;
import com.mpusinhol.rockpaperscissors.model.entity.User;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.model.mapper.GameMapper;
import com.mpusinhol.rockpaperscissors.repository.GameRepository;
import com.mpusinhol.rockpaperscissors.repository.UserRepository;
import com.mpusinhol.rockpaperscissors.service.GameCacheService;
import com.mpusinhol.rockpaperscissors.service.GameService;
import com.mpusinhol.rockpaperscissors.utils.GameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameCacheService gameCacheService;

    @Override
    public GameDTO startGame(String username) {
        UserMoves userMoves = null;

        if (hasText(username) && !ANONYMOUS_USER.equals(username)) {
            userMoves = findLastMovesByUsername(username);
        } else {
            userMoves = new UserMoves(ANONYMOUS_USER, new ArrayList<>(), null);
        }

        GameDTO game = new GameDTO(UUID.randomUUID().toString(), new ArrayList<>(), userMoves, Instant.now(), null, null);
        gameCacheService.saveGame(game);
        return game;
    }

    @Override
    public RoundResponse makeMove(String gameId, Move playerMove) {
        GameDTO gameDTO = findByIdOrThrow(gameId);

        Move computerMove = GameUtils.generateNextMove(gameDTO.userMoves(), gameDTO.rounds());
        RoundResponse round = GameUtils.determineRoundWinner(gameDTO.rounds().size() + 1, computerMove, playerMove);
        gameDTO.userMoves().moves().add(round.roundDTO().playerMove());
        gameDTO.rounds().add(round.roundDTO());
        gameCacheService.saveGame(gameDTO);

        return round;
    }

    @Override
    @Transactional
    public GameStatistics terminateGame(String gameId) {
        GameDTO gameDTO = findByIdOrThrow(gameId);

        GameStatistics gameStatistics = GameUtils.generateStatistics(gameDTO);
        Game game = GameMapper.toEntity(gameDTO, gameStatistics);
        game.getRounds().forEach(round -> round.setGame(game));

        if (!isNull(gameDTO.userMoves())) {
            User user = userRepository.findByUsername(gameDTO.userMoves().username())
                    .orElse(null);
            game.setUser(user);
        }

        gameCacheService.deleteGame(gameId);
        gameRepository.save(game);

        return gameStatistics;
    }

    private GameDTO findByIdOrThrow(String gameId) {
        return gameCacheService.getGame(gameId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Game %s does not exist or is not ongoing".formatted(gameId)));
    }

    private UserMoves findLastMovesByUsername(String username) {
        List<Move> moves = gameRepository.findLastPlayerMovesByUsername(username, PageRequest.of(0, 50));
        Move mostPlayedMove = moves.stream()
                    .collect(groupingBy(Function.identity(), counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

        return new UserMoves(username, moves, mostPlayedMove);
    }
}
