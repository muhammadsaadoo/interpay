package com.fintech.user_service.repositories;


import com.fintech.user_service.entities.KycDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocumentEntity, UUID> {

    List<KycDocumentEntity> findByUser_UserId(UUID userId);

    List<KycDocumentEntity> findByVerificationStatus(String status);
}

