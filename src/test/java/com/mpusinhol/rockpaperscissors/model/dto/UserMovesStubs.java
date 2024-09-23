package com.mpusinhol.rockpaperscissors.model.dto;

import com.mpusinhol.rockpaperscissors.model.enumeration.Move;

import java.util.ArrayList;
import java.util.List;

import static com.mpusinhol.rockpaperscissors.controller.filter.AuthenticationFilter.ANONYMOUS_USER;

public class UserMovesStubs {

    public static UserMoves createAnonymousUserMoves() {
        return new UserMoves(ANONYMOUS_USER, new ArrayList<>(), null);
    }

    public static UserMoves createAuthenticatedUserMoves() {
        List<Move> moves = new ArrayList<>(List.of(Move.ROCK, Move.PAPER, Move.PAPER));
        return new UserMoves("mpusinhol", moves, Move.PAPER);
    }
}
