package com.bistu.clubsystembackend.util;

import com.bistu.clubsystembackend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Long userId, String username, String permissionType) {
        return generateToken(userId, username, permissionType, jwtProperties.getExpireSeconds(), "ACCESS");
    }

    public String generateRefreshToken(Long userId, String username, String permissionType) {
        return generateToken(userId, username, permissionType, 7 * 24 * 3600L, "REFRESH");
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(buildKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(BizCode.LOGIN_EXPIRED);
        }
    }

    private String generateToken(Long userId, String username, String permissionType, long expireSeconds, String tokenType) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expireSeconds);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("permissionType", permissionType);
        claims.put("tokenType", tokenType);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(buildKey())
                .compact();
    }

    private SecretKey buildKey() {
        byte[] key = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(key);
    }
}
