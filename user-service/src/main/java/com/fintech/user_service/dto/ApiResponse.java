package com.fintech.user_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private String status;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .message(message)
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .status("FAILED")
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
