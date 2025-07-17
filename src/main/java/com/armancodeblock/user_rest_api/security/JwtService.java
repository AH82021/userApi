package com.armancodeblock.user_rest_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds
    private final String PREFIX = "Bearer";
    private static final SecretKey key = Keys.hmacShaKeyFor("your-256-bit-secret-key-here-must-be-at-least-32-characters-long".getBytes());

    // Updated JWT builder methods to avoid deprecated methods
    public String getToken(String username) {
        return Jwts.builder()
                .claim("sub", username) // Replaced setSubject with claim("sub", ...)
                .claim("exp", new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Replaced setExpiration with claim("exp", ...)
                .signWith(key) // Removed explicit algorithm as the key determines it
                .compact();
    }

    // Method to validate the token and extract it from request header
    public String getAuthUser(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(PREFIX + " ")) {
            String user = Jwts.parser()
                    .verifyWith(key) // Use verifyWith for signature verification
                    .build()
                    .parseSignedClaims(token.replace(PREFIX + " ", "")) // Parse the token
                    .getPayload()
                    .getSubject(); // Extract the subject
            if (user != null && !user.isEmpty()) return user;
        }
        return null;
    }

    // Method to extract username from JWT token string
    public String getUsernameFromToken(String token) {
        try {
            String user = Jwts.parser()
                    .verifyWith(key) // Use verifyWith for signature verification
                    .build()
                    .parseSignedClaims(token) // Parse the token
                    .getPayload()
                    .getSubject(); // Extract the subject
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
