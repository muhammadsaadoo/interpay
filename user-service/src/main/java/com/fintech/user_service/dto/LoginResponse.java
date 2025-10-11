package com.fintech.user_service.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String message;
    private String userId;
}

