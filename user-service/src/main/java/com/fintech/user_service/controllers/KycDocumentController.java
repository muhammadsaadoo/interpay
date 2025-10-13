package com.fintech.user_service.controllers;



import com.fintech.user_service.dto.KycDocumentDTO;

import com.fintech.user_service.services.KycDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycDocumentController {

    private final KycDocumentService kycDocumentService;

    @PostMapping
    public ResponseEntity<KycDocumentDTO> uploadKyc(@RequestBody KycDocumentDTO dto) {
        return ResponseEntity.ok(kycDocumentService.uploadKyc(dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<KycDocumentDTO>> getKycByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(kycDocumentService.getKycByUser(userId));
    }
}

