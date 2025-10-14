package com.fintech.user_service.services;



import com.fintech.user_service.dto.*;
import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.entities.UserProfileEntity;
import com.fintech.user_service.entities.KycDocumentEntity;
import com.fintech.user_service.exception.ResourceNotFoundException;
import com.fintech.user_service.repositories.KycDocumentRepository;
import com.fintech.user_service.repositories.UserProfileRepository;
import com.fintech.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final KycDocumentRepository kycDocumentRepository;

    public UserDetailsResponse getUserDetails(UUID userId) {
        // Get user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Convert user entity to DTO
        UserDTO userDTO = UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .userStatus(user.getUserStatus())
                .accountType(user.getAccountType())
                .kycStatus(user.getKycStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();

        // Get profile
        UserProfileEntity profile = userProfileRepository.findByUser_UserId(userId).orElse(null);
        UserProfileDTO profileDTO = null;

        if (profile != null) {
            profileDTO = UserProfileDTO.builder()
                    .profileId(profile.getProfileId())
                    .userId(userId)
                    .firstName(profile.getFirstName())
                    .lastName(profile.getLastName())
                    .dateOfBirth(profile.getDateOfBirth())
                    .countryCode(profile.getCountryCode())
                    .addressLine1(profile.getAddressLine1())
                    .addressLine2(profile.getAddressLine2())
                    .city(profile.getCity())
                    .state(profile.getState())
                    .postalCode(profile.getPostalCode())
                    .createdAt(profile.getCreatedAt())
                    .updatedAt(profile.getUpdatedAt())
                    .build();
        }

        // Get KYC list
        List<KycDocumentDTO> kycDocs = kycDocumentRepository.findByUser_UserId(userId)
                .stream()
                .map(doc -> KycDocumentDTO.builder()
                        .documentId(doc.getDocumentId())
                        .userId(userId)
                        .documentType(doc.getDocumentType())
                        .documentNumber(doc.getDocumentNumber())
                        .documentUrl(doc.getDocumentUrl())
                        .verificationStatus(doc.getVerificationStatus())
                        .verifiedAt(doc.getVerifiedAt())
                        .verifiedBy(doc.getVerifiedBy())
                        .createdAt(doc.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return UserDetailsResponse.builder()
                .user(userDTO)
                .profile(profileDTO)
                .kycDocuments(kycDocs)
                .build();
    }
}

