package com.fintech.user_service.dto;



import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocumentDTO {
    private UUID documentId;
    private UUID userId;
    private String documentType;
    private String documentNumber;
    private String documentUrl;
    private String verificationStatus;
    private LocalDateTime verifiedAt;
    private UUID verifiedBy;
    private LocalDateTime createdAt;
}

