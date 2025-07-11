package com.eaglebank.bankapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMilliseconds = 3600000; // 1 hour

    // Generate token
    public String generateToken(String username, Set<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", new ArrayList<>(roles));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // Extract username/email from token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Helper to parse token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
