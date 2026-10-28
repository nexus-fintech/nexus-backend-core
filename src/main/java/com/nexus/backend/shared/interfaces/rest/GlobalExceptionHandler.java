package com.nexus.backend.shared.interfaces.rest;

import com.nexus.backend.shared.interfaces.rest.resources.MessageResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates domain exceptions into HTTP answers.
 * Commands and value objects reject bad input with IllegalArgumentException, and aggregates
 * reject invalid transitions (e.g. approving a loan twice) with IllegalStateException.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResource> handleInvalidInput(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MessageResource> handleInvalidState(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResource(exception.getMessage()));
    }
}
