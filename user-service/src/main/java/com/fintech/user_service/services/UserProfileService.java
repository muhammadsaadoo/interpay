package com.fintech.user_service.services;



import com.fintech.user_service.dto.UserProfileDTO;
import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.entities.UserProfileEntity;

import com.fintech.user_service.repositories.UserProfileRepository;
import com.fintech.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileDTO createUserProfile(UserProfileDTO dto) {

        // Check if user exists
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        // Convert DTO to Entity
        UserProfileEntity entity = UserProfileEntity.builder()
                .profileId(UUID.randomUUID())
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .countryCode(dto.getCountryCode())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save entity
        UserProfileEntity saved = userProfileRepository.save(entity);

        // Convert back to DTO
        return UserProfileDTO.builder()
                .profileId(saved.getProfileId())
                .userId(saved.getUser().getUserId())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .dateOfBirth(saved.getDateOfBirth())
                .countryCode(saved.getCountryCode())
                .addressLine1(saved.getAddressLine1())
                .addressLine2(saved.getAddressLine2())
                .city(saved.getCity())
                .state(saved.getState())
                .postalCode(saved.getPostalCode())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}

