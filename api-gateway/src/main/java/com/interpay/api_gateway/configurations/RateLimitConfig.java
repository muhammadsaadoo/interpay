package com.interpay.api_gateway.configurations;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Strategy 1: By User ID (if authenticated)
            return exchange.getPrincipal()
                    .map(principal -> "user_" + principal.getName())
                    .switchIfEmpty(
                            // Strategy 2: By IP Address (for anonymous users)
                            Mono.just("ip_" + exchange.getRequest()
                                    .getRemoteAddress()
                                    .getAddress()
                                    .getHostAddress())
                    );
        };
    }
}


