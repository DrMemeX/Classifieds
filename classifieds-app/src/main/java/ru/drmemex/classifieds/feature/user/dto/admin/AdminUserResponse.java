package ru.drmemex.classifieds.feature.user.dto.admin;

import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record AdminUserResponse(

        Long userId,
        String login,
        UserRole role,
        UserStatus status,
        String firstName,
        String lastName,
        String phone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime blockedAt

) {
}
