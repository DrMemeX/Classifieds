package ru.drmemex.classifieds.feature.user.dto.admin;

public record UpdateAdminUserRequest(

        String login,
        String firstName,
        String lastName,
        String phone

) {
}
