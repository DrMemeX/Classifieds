package ru.drmemex.classifieds.feature.user.dto.auth.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordUserRequest(

        @NotBlank(message = "Current password cannot be blank")
        String currentPassword,

        @NotBlank(message = "New password cannot be blank")
        @Size(
                min = 8,
                max = 64,
                message = "Password must contain from 8 to 64 characters"
        )
        String newPassword

) {
}