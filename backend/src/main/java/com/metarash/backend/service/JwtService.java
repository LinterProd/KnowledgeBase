package com.metarash.backend.service;

import com.metarash.backend.model.entity.User;
import com.metarash.backend.service.props.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Getter private final SecretKey accessSecret;
    @Getter private final SecretKey refreshSecret;
    @Getter private final Duration accessExpiration;
    @Getter private final Duration refreshExpiration;

    public JwtService(JwtProperties jwtProperties) {
        this.accessSecret = Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshSecret = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = jwtProperties.getAccessExpiration();
        this.refreshExpiration = jwtProperties.getRefreshExpiration();
    }

    public String generateAccessToken(Long userId, String username, User.UserRole role) {
        return buildToken(userId, accessSecret, accessExpiration, username, role);
    }

    public String generateRefreshToken(Long userId) {
        return buildToken(userId, refreshSecret, refreshExpiration, null, null);
    }

    public Claims parseClaims(String token, boolean isRefresh) {
        SecretKey key = isRefresh ? refreshSecret : accessSecret;
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.debug("Token error: {}", e.getMessage());
            throw e;
        }
    }

    private String buildToken(Long userId, SecretKey key, Duration expiration, String username, User.UserRole role) {
        JwtBuilder builder = Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(expiration)));
        if (username != null) {
            builder.claim("username", username);
        }
        if (role != null) {
            builder.claim("role", role.name());
        }
        return builder.signWith(key).compact();
    }

    public Long getUserId(String token, boolean isRefresh) {
        Claims claims = parseClaims(token, isRefresh);
        return Long.parseLong(claims.getSubject());
    }

    public String getUsername(String token) {
        Claims claims = parseClaims(token, false);
        return claims.get("username", String.class);
    }

    public User.UserRole getRole(String token) {
        Claims claims = parseClaims(token, false);
        return User.UserRole.valueOf(claims.get("role", String.class));
    }

    public boolean validateToken(String token, boolean isRefresh) {
        try {
            parseClaims(token, isRefresh);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}