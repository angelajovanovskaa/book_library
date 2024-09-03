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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {
    private final SecretKey securityKey;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private static final Date VALID_UNTIL = Date.from(Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(30)));
    private static final Date CURRENT_DATE = Date.from(Instant.now());

    public String generateToken(String email) throws IOException {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) throws IOException {
        if (userRepository.findByEmail(email).isPresent()) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(CURRENT_DATE)
                    .setExpiration(VALID_UNTIL)
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    private Key getSignKey() throws IOException {
        Resource resource = resourceLoader.getResource(securityKey.secret_key());
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) throws IOException {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws IOException {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws IOException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) throws IOException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) throws IOException {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws IOException {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}