package com.mpusinhol.rockpaperscissors.controller;

import com.mpusinhol.rockpaperscissors.model.dto.GameDTO;
import com.mpusinhol.rockpaperscissors.model.dto.GameStatistics;
import com.mpusinhol.rockpaperscissors.model.dto.RoundResponse;
import com.mpusinhol.rockpaperscissors.model.enumeration.Move;
import com.mpusinhol.rockpaperscissors.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;
import static java.util.Objects.isNull;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<Void> startGame() {
        String username = switch (SecurityContextHolder.getContext().getAuthentication().getPrincipal()) {
            case UserDetails userDetails -> userDetails.getUsername();
            default -> ANONYMOUS_USER;
        };

        GameDTO gameDTO = gameService.startGame(username);
        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(gameDTO.id())
                        .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<RoundResponse> makeMove(@PathVariable String id, @RequestBody Move move) {
        return ResponseEntity.ok(gameService.makeMove(id, move));
    }

    @PostMapping("/{id}/terminate")
    public ResponseEntity<GameStatistics> terminateGame(@PathVariable String id) {
        return ResponseEntity.ok(gameService.terminateGame(id));
    }
}
