package com.interpay.api_gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "User service is currently unavailable. Please try again later.");
        response.put("timestamp", Instant.now());
        response.put("suggested_retry_after", "30 seconds");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }


}