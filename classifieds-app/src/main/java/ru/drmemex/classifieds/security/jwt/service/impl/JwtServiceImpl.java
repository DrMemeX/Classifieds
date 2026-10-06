package ru.drmemex.classifieds.security.jwt.service.impl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.jwt.JwtProperties;
import ru.drmemex.classifieds.security.jwt.service.JwtService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(User user) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + jwtProperties.expiration()
        );

        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(user.getLogin())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    @Override
    public String extractLogin(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {

        try {
            extractLogin(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                jwtProperties.secret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}
