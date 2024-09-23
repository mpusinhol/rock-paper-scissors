package com.mpusinhol.rockpaperscissors.model.entity;

public class UserStubs {

    public static User createUser() {
        return User.builder()
                .id(1)
                .username("mpusinhol")
                .password("$2a$12$g/VloSNznxumx8ZoDROXMOHXAkMmRpshLFzXcuUCJ4Vrlowdav.1i")
                .build();
    }
}
