package com.interpay.api_gateway.jwt;



import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Handle different role claim formats
            Object roleClaim = claims.get("role");

            if (roleClaim instanceof String) {
                return (String) roleClaim;
            } else if (roleClaim instanceof List) {
                // Handle case where roles are in a list
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) roleClaim;
                return roles.stream()
                        .filter(role -> role.equals("ADMIN") || role.equals("USER"))
                        .findFirst()
                        .orElse("USER");
            }

            return "USER"; // Default role
        } catch (Exception e) {
            System.err.println("Error extracting role from JWT: " + e.getMessage());
            return "USER";
        }
    }

    public boolean hasAdminRole(String token) {
        String role = extractRole(token);
        return "ADMIN".equals(role);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}