CREATE TABLE IF NOT EXISTS user_app (
    id                  SERIAL NOT NULL,
    username            VARCHAR NOT NULL,
    password            VARCHAR NOT NULL,

    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uk_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS game (
    id                      VARCHAR NOT NULL,
    start_time              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    rounds_won_by_computer  INTEGER,
    rounds_won_by_player    INTEGER,
    rounds_tied             INTEGER,
    player_most_played_move VARCHAR,
    winner                  VARCHAR NOT NULL,
    user_id                 INTEGER,

    CONSTRAINT pk_game PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user_app (id)
);

CREATE TABLE IF NOT EXISTS round (
    id                  SERIAL NOT NULL,
    round_number        INTEGER NOT NULL,
    computer_move       VARCHAR NOT NULL,
    player_move         VARCHAR NOT NULL,
    winner              VARCHAR NOT NULL,
    game_id             VARCHAR NOT NULL,

    CONSTRAINT pk_round PRIMARY KEY (id),
    CONSTRAINT fk_game_id FOREIGN KEY (game_id) REFERENCES game (id)
);
