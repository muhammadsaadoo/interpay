package com.fintech.user_service.exception;



import com.fintech.user_service.dto.UserRegistrationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<UserRegistrationResponse> handleExists(ResourceAlreadyExistsException ex) {
        UserRegistrationResponse body = UserRegistrationResponse.builder()
                .message(ex.getMessage())
                .status("FAILED")
                .statusCode(HttpStatus.CONFLICT.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserRegistrationResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Validation error");

        UserRegistrationResponse body = UserRegistrationResponse.builder()
                .message(msg)
                .status("FAILED")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserRegistrationResponse> handleGeneric(Exception ex) {
        UserRegistrationResponse body = UserRegistrationResponse.builder()
                .message(ex.getMessage())
                .status("FAILED")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<UserRegistrationResponse> handleNotFound(ResourceNotFoundException ex) {
        UserRegistrationResponse body = UserRegistrationResponse.builder()
                .message(ex.getMessage())
                .status("FAILED")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

}

