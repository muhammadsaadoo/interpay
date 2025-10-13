package com.fintech.user_service.services;



import com.fintech.user_service.dto.KycDocumentDTO;
import com.fintech.user_service.entities.KycDocumentEntity;
import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.exception.ResourceNotFoundException;
import com.fintech.user_service.repositories.KycDocumentRepository;
import com.fintech.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KycDocumentService {

    private final UserRepository userRepository;
    private final KycDocumentRepository kycDocumentRepository;

    public KycDocumentDTO uploadKyc(KycDocumentDTO dto) {

        // Check if user exists
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        // Create entity from DTO
        KycDocumentEntity entity = KycDocumentEntity.builder()
                .user(user)
                .documentType(dto.getDocumentType())
                .documentNumber(dto.getDocumentNumber())
                .documentUrl(dto.getDocumentUrl())
                .verificationStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        KycDocumentEntity saved = kycDocumentRepository.save(entity);

        return toDTO(saved);
    }

    public List<KycDocumentDTO> getKycByUser(UUID userId) {
        List<KycDocumentEntity> documents = kycDocumentRepository.findByUser_UserId(userId);
        if (documents.isEmpty()) {
            throw new ResourceNotFoundException("No KYC documents found for user id: " + userId);
        }
        return documents.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private KycDocumentDTO toDTO(KycDocumentEntity entity) {
        return KycDocumentDTO.builder()
                .documentId(entity.getDocumentId())
                .userId(entity.getUser().getUserId())
                .documentType(entity.getDocumentType())
                .documentNumber(entity.getDocumentNumber())
                .documentUrl(entity.getDocumentUrl())
                .verificationStatus(entity.getVerificationStatus())
                .verifiedAt(entity.getVerifiedAt())
                .verifiedBy(entity.getVerifiedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

