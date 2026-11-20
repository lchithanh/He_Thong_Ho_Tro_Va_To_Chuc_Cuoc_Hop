package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        this.expiration = expiration;
    }

    // Tạo JWT token
    public String generateToken(String username, String maVaiTro) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .claim("role", maVaiTro)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Lấy tên đăng nhập từ token
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    public PhienJwt docPhien(String token) {
        Claims claims = extractClaims(token);
        if (claims.getSubject() == null || claims.getSubject().isBlank() || claims.getExpiration() == null) {
            throw new JwtException("JWT thiếu thông tin phiên");
        }
        return new PhienJwt(claims.getSubject(), claims.getExpiration().toInstant());
    }

    public record PhienJwt(String tenDangNhap, Instant hetHanLuc) {}

    // Kiểm tra token hợp lệ và chưa hết hạn
    public boolean isTokenValid(String token, String username) {

        try {
            String tokenUsername = extractUsername(token);

            return tokenUsername.equals(username)
                    && extractClaims(token).getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Đọc thông tin trong token
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}