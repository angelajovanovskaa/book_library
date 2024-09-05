package com.kinandcarta.book_library.config;

import com.kinandcarta.book_library.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtConfigurationProperties jwtConfigurationProperties;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;

    public String generateToken(String email) throws IOException {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) throws IOException {
        if (userRepository.findByEmail(email).isPresent()) {
            Date currentDate = Date.from(Instant.now());
            Date tokenValidUntillDate = Date.from(
                    Instant.now().plus(jwtConfigurationProperties.getJwtExpirationTime(), ChronoUnit.MINUTES));

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(currentDate)
                    .setExpiration(tokenValidUntillDate)
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    public String extractEmail(String token) throws IOException {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims extractAllClaims(String token) throws IOException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws IOException {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Key getSignKey() throws IOException {
        Resource resource = resourceLoader.getResource(jwtConfigurationProperties.getSecretKey());
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date extractExpiration(String token) throws IOException {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws IOException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) throws IOException {
        return extractExpiration(token).before(new Date());
    }
}