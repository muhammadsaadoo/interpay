package com.fintech.user_service.controllers;



import com.fintech.user_service.dto.UserProfileDTO;

import com.fintech.user_service.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileDTO> createProfile(@RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(userProfileService.createUserProfile(dto));
    }
}

