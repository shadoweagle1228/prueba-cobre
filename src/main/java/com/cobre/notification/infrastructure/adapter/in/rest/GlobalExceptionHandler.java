package com.cobre.notification.infrastructure.adapter.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());

        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            body.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } else if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unauthorized")) {
            body.put("status", HttpStatus.FORBIDDEN.value());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        } else {
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
