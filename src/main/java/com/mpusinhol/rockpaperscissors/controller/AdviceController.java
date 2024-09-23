package com.mpusinhol.rockpaperscissors.controller;

import com.mpusinhol.rockpaperscissors.exception.ObjectDuplicateException;
import com.mpusinhol.rockpaperscissors.exception.ObjectNotFoundException;
import com.mpusinhol.rockpaperscissors.model.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(ObjectDuplicateException.class)
    public ResponseEntity<ExceptionDTO> objectDuplicate(ObjectDuplicateException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), Instant.now(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptionDTO);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDTO> authenticationException(AuthenticationException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), Instant.now(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionDTO);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ExceptionDTO> objectDuplicate(ObjectNotFoundException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.NOT_FOUND.value(), Instant.now(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exceptionDTO);
    }
}
