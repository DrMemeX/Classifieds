package ru.drmemex.classifieds.security.jwt.service.impl;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.security.jwt.JwtProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceImplTest {

    private static final String SECRET =
            "12345678901234567890123456789012";

    private static final String ANOTHER_SECRET =
            "65432109876543210987654321098765";

    private static final long EXPIRATION =
            3_600_000L;

    private final JwtServiceImpl jwtService =
            new JwtServiceImpl(
                    new JwtProperties(
                            SECRET,
                            EXPIRATION
                    )
            );

    @Test
    void generateAccessToken_ShouldGenerateToken() {

        User user = new User();
        user.setLogin("user");
        user.setRole(UserRole.USER);

        String token =
                jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractLogin_ShouldReturnLogin() {

        User user = new User();
        user.setLogin("user");
        user.setRole(UserRole.USER);

        String token =
                jwtService.generateAccessToken(user);

        String login =
                jwtService.extractLogin(token);

        assertEquals(
                "user",
                login
        );
    }

    @Test
    void extractLogin_ShouldThrowException_WhenTokenIsInvalid() {

        assertThrows(
                JwtException.class,
                () -> jwtService.extractLogin(
                        "invalid-token"
                )
        );
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {

        User user = new User();
        user.setLogin("user");
        user.setRole(UserRole.USER);

        String token =
                jwtService.generateAccessToken(user);

        assertTrue(
                jwtService.isTokenValid(token)
        );
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenHasInvalidSignature() {

        User user = new User();
        user.setLogin("user");
        user.setRole(UserRole.USER);

        JwtServiceImpl anotherJwtService =
                new JwtServiceImpl(
                        new JwtProperties(
                                ANOTHER_SECRET,
                                EXPIRATION
                        )
                );

        String token =
                anotherJwtService.generateAccessToken(user);

        assertFalse(
                jwtService.isTokenValid(token)
        );
    }
}