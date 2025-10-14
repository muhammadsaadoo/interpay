package com.fintech.user_service.controllers;


import com.fintech.user_service.dto.UserDetailsResponse;
import com.fintech.user_service.services.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @GetMapping("/{userId}/details")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userDetailsService.getUserDetails(userId));
    }
}

