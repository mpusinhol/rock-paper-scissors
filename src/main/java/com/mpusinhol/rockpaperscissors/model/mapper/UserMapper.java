package com.mpusinhol.rockpaperscissors.model.mapper;

import com.mpusinhol.rockpaperscissors.model.dto.UserDTO;
import com.mpusinhol.rockpaperscissors.model.entity.User;

public class UserMapper {

    public static User toEntity(UserDTO userDTO) {
        return User.builder()
                .username(userDTO.username())
                .password(userDTO.password())
                .build();
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), null);
    }
}
