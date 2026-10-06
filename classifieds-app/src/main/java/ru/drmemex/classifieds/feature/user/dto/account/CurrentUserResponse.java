package ru.drmemex.classifieds.feature.user.dto.account;

public record CurrentUserResponse(

        Long userId,
        String login,
        String role,
        String status,
        String firstName,
        String lastName,
        String phone

) {
}
