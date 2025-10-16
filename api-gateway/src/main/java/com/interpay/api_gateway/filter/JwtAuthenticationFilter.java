package com.interpay.api_gateway.filter;




import com.interpay.api_gateway.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Configuration properties if needed
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Check if this path requires authentication
            if (isAuthMissing(request)) {
                return this.onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            // 2. Extract token from header
            String token = getAuthHeader(request);
            if (token == null || !token.startsWith("Bearer ")) {
                return this.onError(exchange, "Invalid authorization header format", HttpStatus.UNAUTHORIZED);
            }

            // 3. Remove "Bearer " prefix
            token = token.substring(7);

            // 4. Validate JWT token
            if (!jwtUtil.validateToken(token)) {
                return this.onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
            }

            // 5. Extract user info and add to headers for downstream services
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            // 6. Add user details to request headers for microservices
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", username)
                    .header("X-User-Role", role != null ? role : "USER")
                    .header("X-Authenticated", "true")
                    .build();

            // 7. Continue with the modified request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        // List of public endpoints that don't require authentication
        String path = request.getPath().toString();

        // Public endpoints (adjust according to your needs)
        if (path.startsWith("/auth/login") ||
                path.startsWith("/auth/signup") ||
                path.startsWith("/auth/register") ||
                path.startsWith("/actuator/health") ||
                path.equals("/") || path.equals("/favicon.ico")) {
            return false; // Don't check auth for these
        }

        // Check if Authorization header is present for protected endpoints
        return !request.getHeaders().containsKey("Authorization");
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getFirst("Authorization");
    }

    private Mono<Void> onError(org.springframework.web.server.ServerWebExchange exchange,
                               String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String errorJson = String.format("{\"error\": \"%s\", \"status\": %d, \"path\": \"%s\"}",
                err, httpStatus.value(), exchange.getRequest().getPath());

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorJson.getBytes()))
        );
    }
}
