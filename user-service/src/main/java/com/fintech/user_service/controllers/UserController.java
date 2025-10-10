package com.fintech.user_service.controllers;



import com.fintech.user_service.dto.UserRegistrationRequest;
import com.fintech.user_service.dto.UserRegistrationResponse;

import com.fintech.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserRegistrationResponse resp = userService.registerUser(request);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }
}

