package com.mpusinhol.rockpaperscissors.model.enumeration;

public enum Move {
    ROCK,
    PAPER,
    SCISSORS;

    public int compareMove(Move otherMove) {
        if (this == otherMove) {
            return 0;
        }

        return switch (otherMove) {
            case ROCK -> this == PAPER ? 1 : -1;
            case PAPER -> this == SCISSORS ? 1 : -1;
            case SCISSORS -> this == ROCK ? 1 : -1;
        };
    }
}
