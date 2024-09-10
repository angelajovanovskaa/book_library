package com.kinandcarta.book_library.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final ResourceLoader resourceLoader;

    public String generateToken(String email) throws IOException {
        return createToken(email);
    }

    private String createToken(String email) throws IOException {
        Instant currentInstant = Instant.now();
        Date currentDate = Date.from(currentInstant);
        int expirationTimeInMinutes = jwtConfigurationProperties.getExpirationTimeInMinutes();
        Instant expirationInstant = currentInstant.plus(expirationTimeInMinutes, ChronoUnit.MINUTES);
        Date tokenExpirationDate = Date.from(expirationInstant);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(currentDate)
                .setExpiration(tokenExpirationDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) throws IOException {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) throws IOException {
        final String email = extractEmail(token);
        boolean emailMatchesUsername = email.equals(userDetails.getUsername());
        return emailMatchesUsername && !isTokenExpired(token);
    }

    private Date extractExpiration(String token) throws IOException {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws IOException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() throws IOException {
        String secretKeyPath = jwtConfigurationProperties.getSecretKey();
        Resource resource = resourceLoader.getResource(secretKeyPath);
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws IOException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) throws IOException {
        Date tokenExpirationDate = extractExpiration(token);
        return tokenExpirationDate.before(new Date());
    }
}