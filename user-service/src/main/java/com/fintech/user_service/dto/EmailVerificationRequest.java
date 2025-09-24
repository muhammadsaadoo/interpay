package com.fintech.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
public class EmailVerificationRequest {
    // Getters and Setters
    @NotBlank
    private String email;

    @NotBlank
    private String verificationCode;
}

