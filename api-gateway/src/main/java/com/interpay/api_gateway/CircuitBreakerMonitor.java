package com.interpay.api_gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerMonitor {

    @EventListener
    public void handleCircuitBreakerEvent(CircuitBreakerOnStateTransitionEvent event) {
        String circuitBreakerName = event.getCircuitBreakerName();
        CircuitBreaker.State state = event.getStateTransition().getToState();

        switch (state) {
            case CLOSED:
                System.out.println("✅ Circuit Breaker " + circuitBreakerName + " is CLOSED - Normal operation");
                break;
            case OPEN:
                System.out.println("🔴 Circuit Breaker " + circuitBreakerName + " is OPEN - Blocking requests");
                break;
            case HALF_OPEN:
                System.out.println("🟡 Circuit Breaker " + circuitBreakerName + " is HALF_OPEN - Testing recovery");
                break;
        }
    }
}
