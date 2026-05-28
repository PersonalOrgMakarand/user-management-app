package com.example.usermanagement.security;

import com.example.usermanagement.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${auth.jwt.secret}") final String secret,
            @Value("${auth.jwt.expiration-ms:3600000}") final long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(final User user) {
        final Instant now = Instant.now();
        final Instant expiresAt = now.plusMillis(expirationMs);

        return Jwts.builder()
                .claims(Map.of("role", user.getRole(), "name", user.getName()))
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(final String token) {
        return parseClaims(token).getSubject();
    }

    public Date extractExpiration(final String token) {
        return parseClaims(token).getExpiration();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public boolean isTokenValid(final String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration != null && expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims parseClaims(final String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
