package com.fintech.user_service.dto;



import com.fintech.user_service.dto.enums.AccountType;
import com.fintech.user_service.dto.enums.Role;
import com.fintech.user_service.dto.enums.UserStatus;
import com.fintech.user_service.entities.UserEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserRegistrationResponse {
    private String userId;
    private String email;
    private AccountType accountType;
    private UserStatus userStatus;
    private Role role;
    private String status;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
}

