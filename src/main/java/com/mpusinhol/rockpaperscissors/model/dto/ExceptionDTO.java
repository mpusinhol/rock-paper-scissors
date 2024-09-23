package com.mpusinhol.rockpaperscissors.model.dto;

import java.time.Instant;

public record ExceptionDTO(Integer code, Instant time, String message) {}
