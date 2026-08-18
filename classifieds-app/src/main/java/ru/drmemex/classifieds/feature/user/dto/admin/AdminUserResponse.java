package ru.drmemex.classifieds.feature.user.dto.admin;

import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;

import java.time.LocalDateTime;

public record AdminUserResponse(

        Long userId,
        String login,
        UserRole role,
        UserStatus status,
        String firstName,
        String lastName,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime blockedAt

) {
}
