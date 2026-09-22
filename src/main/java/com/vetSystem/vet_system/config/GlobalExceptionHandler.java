package com.vetSystem.vet_system.config;

import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceInUseException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<Map<String, String>> handleResourceInUse(ResourceInUseException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler(InvalidResourceException.class)
    public ResponseEntity<Map<String, String>> handleInvalid(InvalidResourceException exception) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", exception.getMessage()));
    }
}
