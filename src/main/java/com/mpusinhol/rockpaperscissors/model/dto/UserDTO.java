package com.mpusinhol.rockpaperscissors.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Integer id,
        @NotEmpty(message = "Username must not be empty")
        String username,
        @NotEmpty(message = "Password must not be empty")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password) {
}
