package com.fintech.user_service.dto;



import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserRegistrationResponse {
    private String userId;
    private String email;
    private String accountType;
    private String status;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
}

