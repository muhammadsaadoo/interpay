package com.fintech.user_service.dto;



import com.fintech.user_service.entities.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID userId;
    private String email;
    private String phone;
    private UserEntity.UserStatus userStatus;
    private UserEntity.AccountType accountType;
    private UserEntity.KycStatus kycStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private UserEntity.Role role;
}

