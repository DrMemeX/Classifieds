package ru.drmemex.classifieds.feature.user.dto.auth.register;

import ru.drmemex.classifieds.feature.user.model.UserRole;

public record RegisterUserResponse(

        Long userId,
        String login,
        String firstName,
        String lastName,
        String phone,
        UserRole role

) {
}
