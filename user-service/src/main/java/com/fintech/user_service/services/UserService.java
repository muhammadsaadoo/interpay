package com.fintech.user_service.services;


import com.fintech.user_service.dto.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fintech.user_service.dto.UserDTO;
import com.fintech.user_service.dto.UserRegistrationRequest;
import com.fintech.user_service.dto.UserRegistrationResponse;
import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.exception.ResourceAlreadyExistsException;

import com.fintech.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // injected bean


    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    private UserDTO convertToDTO(UserEntity user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userStatus(user.getUserStatus())
                .accountType(user.getAccountType())
                .kycStatus(user.getKycStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .role(user.getRole())
                .build();
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new ResourceAlreadyExistsException("Phone already registered");
        }

        UserEntity user = UserEntity.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .password(passwordEncoder.encode(req.getPassword()))
                .accountType(AccountType.valueOf(req.getAccountType()))
                .userStatus(UserStatus.ACTIVE)
                .kycStatus(KycStatus.PENDING)
                // createdAt and updatedAt are set by entity defaults
                .build();

        UserEntity saved = userRepository.save(user);

        return UserRegistrationResponse.builder()
                .userId(saved.getUserId().toString())
                .email(saved.getEmail())
                .accountType(saved.getAccountType())
                .userStatus(saved.getUserStatus())
                .role(saved.getRole())
                .status("Created")
                .message("User registered successfully")

                .statusCode(201)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

