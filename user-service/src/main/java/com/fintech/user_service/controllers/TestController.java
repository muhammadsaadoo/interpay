package com.fintech.user_service.controllers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Add this to your User Service for testing
@RestController
@RequestMapping("/api")
public class TestController {

    private static boolean shouldFail = false;

    // Endpoint that can be made to fail
    @GetMapping("/test/circuit")
    public String testCircuitBreaker() {
        if (shouldFail) {
            throw new RuntimeException("Simulated service failure");
        }
        return "Service is working normally";
    }


    // Endpoint to toggle failure mode
    @PostMapping("/test/toggle-failure")
    public String toggleFailure() {
        shouldFail = !shouldFail;
        return "Failure mode: " + shouldFail;
    }

    // Slow endpoint for timeout testing
    @GetMapping("/test/slow")
    public String slowEndpoint() throws InterruptedException {
        Thread.sleep(3000); // 3 second delay
        return "Slow response completed";
    }

    // Add to User Service TestController
    private static int callCount = 0;

    @GetMapping("/test/flaky")
    public String flakyEndpoint() {
        callCount++;
        System.out.println("📞 Flaky endpoint call #" + callCount);

        if (callCount % 3 != 0) { // Fail first two calls, succeed on third
            throw new RuntimeException("Flaky service failure - call " + callCount);
        }

        callCount = 0; // Reset for next test
        return "Success after retries!";
    }

    @GetMapping("/test/reset")
    public String resetCounter() {
        callCount = 0;
        return "Counter reset";
    }



    @GetMapping("/test/instance")
    public String getInstanceInfo() {

        return "Response from instance: " + "3";
    }
}
