package ru.drmemex.classifieds.feature.user.dto.auth.login;

import ru.drmemex.classifieds.feature.user.model.UserRole;

public record LoginUserResponse(

        String accessToken,
        String tokenType,

        Long userId,
        String login,
        String firstName,
        String lastName,
        UserRole role
) {
}
