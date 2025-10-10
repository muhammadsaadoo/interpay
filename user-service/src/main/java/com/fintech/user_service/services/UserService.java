package com.fintech.user_service.services;



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
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .accountType(req.getAccountType())
                .status("ACTIVE")
                .kycStatus("PENDING")
                // createdAt and updatedAt are set by entity defaults
                .build();

        UserEntity saved = userRepository.save(user);

        return UserRegistrationResponse.builder()
                .userId(saved.getUserId().toString())
                .email(saved.getEmail())
                .accountType(saved.getAccountType())
                .status(saved.getStatus())
                .message("User registered successfully")
                .statusCode(201)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

