package com.interpay.api_gateway.filter;




import com.interpay.api_gateway.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AdminRoleValidationFilter extends AbstractGatewayFilterFactory<AdminRoleValidationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AdminRoleValidationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Configuration properties if needed
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Get Authorization header
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Authorization header missing", HttpStatus.UNAUTHORIZED);
            }

            // 2. Extract token
            String token = authHeader.substring(7);

            // 3. Validate token
            if (!jwtUtil.validateToken(token)) {
                return this.onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }

            // 4. Check if user has ADMIN role
            String role = jwtUtil.extractRole(token);
            if (role == null || !role.equals("ROLE_ADMIN")) {
                System.out.println(role);
                return this.onError(exchange, "Access denied. ADMIN role required.", HttpStatus.FORBIDDEN);
            }

            // 5. Extract user info
            String username = jwtUtil.extractUsername(token);

            // 6. Add admin user info to headers for downstream service
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", username)
                    .header("X-User-Role", role)
                    .header("X-User-Admin", "true")
                    .header("X-Authenticated", "true")
                    .build();

            System.out.println("🎯 Admin access granted to: " + username + " for path: " + request.getPath());

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange,
                               String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String errorJson = String.format("{\"error\": \"%s\", \"status\": %d, \"path\": \"%s\", \"timestamp\": \"%s\"}",
                err, httpStatus.value(), exchange.getRequest().getPath(), java.time.Instant.now());

        System.err.println("🔒 Access denied: " + err + " for path: " + exchange.getRequest().getPath());

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorJson.getBytes()))
        );
    }
}
