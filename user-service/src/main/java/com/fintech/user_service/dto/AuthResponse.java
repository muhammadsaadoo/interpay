package com.fintech.user_service.dto;


import com.fintech.user_service.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserEntity user;
}
