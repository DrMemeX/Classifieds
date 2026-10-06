package ru.drmemex.classifieds.security.jwt.service;

import ru.drmemex.classifieds.feature.user.entity.User;

public interface JwtService {

    String generateAccessToken(User user);

    String extractLogin(String token);

    boolean isTokenValid(String token);
}